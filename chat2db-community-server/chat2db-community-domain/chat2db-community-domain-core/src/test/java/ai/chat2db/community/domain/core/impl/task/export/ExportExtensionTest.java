package ai.chat2db.community.domain.core.impl.task.export;

import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;
import ai.chat2db.community.domain.api.model.task.extension.ExportCell;
import ai.chat2db.community.domain.api.model.task.extension.ExportCellContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionOperation;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionPlan;
import ai.chat2db.community.domain.api.service.db.extension.ISqlExecutionPolicy;
import ai.chat2db.community.domain.core.impl.task.export.excel.CsvDataExporter;
import ai.chat2db.community.domain.core.impl.task.export.excel.XlsDataExporter;
import ai.chat2db.community.domain.core.impl.task.export.excel.XlsxDataExporter;
import ai.chat2db.community.domain.core.impl.task.export.json.JsonDataExporter;
import ai.chat2db.community.domain.core.impl.task.export.sql.SqlDataExporter;
import ai.chat2db.community.domain.core.impl.db.extension.SqlExecutionPolicyManager;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.tools.exception.ParamBusinessException;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportExtensionTest {

    @TempDir
    Path tempDir;

    @Test
    void emptyProcessorChainReturnsOriginalCell() {
        ExportCellProcessorChain chain = new ExportCellProcessorChain(List.of());
        ExportCell cell = new ExportCell("visible", Types.VARCHAR, "VARCHAR", 20, 0);

        assertSame(cell, chain.process(cellContext(), cell));
    }

    @Test
    void processorsRunInInjectedOrder() {
        List<String> values = new ArrayList<>();
        ExportCellProcessorChain chain = new ExportCellProcessorChain(List.of(
                (context, cell) -> {
                    values.add(String.valueOf(cell.getValue()));
                    return cell.withValue("first");
                },
                (context, cell) -> {
                    values.add(String.valueOf(cell.getValue()));
                    return cell.withValue("second");
                }));

        ExportCell processed = chain.process(cellContext(),
                new ExportCell("raw", Types.VARCHAR, "VARCHAR", 20, 0));

        assertEquals(List.of("raw", "first"), values);
        assertEquals("second", processed.getValue());
    }

    @Test
    void baseExporterBuildsStructuredCellContextBeforeSerialization() throws Exception {
        AtomicReference<ExportCellContext> capturedContext = new AtomicReference<>();
        ExportCellProcessorChain chain = new ExportCellProcessorChain(List.of((context, cell) -> {
            capturedContext.set(context);
            assertEquals(Types.VARCHAR, cell.getJdbcType());
            assertEquals("VARCHAR", cell.getTypeName());
            return cell.withValue("masked");
        }));
        TestExporter exporter = new TestExporter(chain);
        ExportCell processed = exporter.process(metadata(), "orders", "raw");

        assertEquals("masked", processed.getValue());
        assertEquals("orders", capturedContext.get().getTableName());
        assertEquals("email", capturedContext.get().getColumnName());
        assertEquals("test", capturedContext.get().getExportType());
    }

    @Test
    void baseExporterPassesRawJdbcValueToProcessors() throws Exception {
        BigDecimal rawValue = new BigDecimal("123.4500");
        AtomicReference<Object> capturedValue = new AtomicReference<>();
        ExportCellProcessorChain chain = new ExportCellProcessorChain(List.of((context, cell) -> {
            capturedValue.set(cell.getValue());
            return cell;
        }));
        TestExporter exporter = new TestExporter(chain);

        ExportCell processed = exporter.process(metadata(), "orders", jdbcValue(rawValue));

        assertSame(rawValue, capturedValue.get());
        assertSame(rawValue, processed.getValue());
    }

    @Test
    void registryContainsAllCommunityFormatsAndRejectsDuplicates() {
        ExportCellProcessorChain chain = new ExportCellProcessorChain(List.of());
        SqlExecutionPolicyManager policyManager = policyManager();
        List<IExportStrategy> strategies = List.of(new CsvDataExporter(chain, policyManager),
                new XlsDataExporter(chain, policyManager), new XlsxDataExporter(chain, policyManager),
                new JsonDataExporter(chain, policyManager), new SqlDataExporter(chain, policyManager));
        ExportStrategyRegistry registry = new ExportStrategyRegistry(strategies);

        assertEquals("csv", registry.getExporter("CSV").type());
        assertEquals("xls", registry.getExporter("xls").type());
        assertEquals("xlsx", registry.getExporter("xlsx").type());
        assertEquals("json", registry.getExporter("json").type());
        assertEquals("sql", registry.getExporter("sql").type());
        assertThrows(ParamBusinessException.class, () -> registry.getExporter("xml"));
        assertThrows(IllegalStateException.class,
                () -> new ExportStrategyRegistry(List.of(strategy("csv"), strategy("CSV"))));
    }

    @Test
    void sharedExportRowCursorStopsBeforeReadingPastThePolicyBudget() throws Exception {
        SqlExecutionPolicyManager policyManager = new SqlExecutionPolicyManager(List.of(new ISqlExecutionPolicy() {
            @Override
            public Integer maxRows(SqlExecutionContext context, String sql) {
                return 2;
            }
        }));
        TestExporter exporter = new TestExporter(new ExportCellProcessorChain(List.of()), policyManager);
        SqlExecutionContext context = new SqlExecutionContext(7L, "MYSQL", "shop", null, "orders",
                "select * from orders", SqlExecutionOperation.EXPORT, "test");
        SqlExecutionPlan plan = policyManager.plan(context);
        AtomicInteger nextCalls = new AtomicInteger();
        ResultSet resultSet = (ResultSet) Proxy.newProxyInstance(ExportExtensionTest.class.getClassLoader(),
                new Class<?>[]{ResultSet.class}, (proxy, method, args) -> {
                    if ("next".equals(method.getName())) {
                        nextCalls.incrementAndGet();
                        return true;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });

        assertEquals(true, exporter.next(resultSet, plan, 0));
        assertEquals(true, exporter.next(resultSet, plan, 1));
        assertEquals(false, exporter.next(resultSet, plan, 2));
        assertEquals(2, nextCalls.get());
    }

    @Test
    void sharedColumnSelectionUsesTheSqlPolicyForJdbcMetadata() throws Exception {
        SqlExecutionPolicyManager policyManager = new SqlExecutionPolicyManager(List.of(new ISqlExecutionPolicy() {
            @Override
            public boolean includeColumn(ai.chat2db.community.domain.api.model.sql.extension.SqlResultColumnContext context) {
                return !"secret".equalsIgnoreCase(context.getColumnName());
            }
        }));
        TestExporter exporter = new TestExporter(new ExportCellProcessorChain(List.of()), policyManager);
        SqlExecutionPlan plan = policyManager.plan(new SqlExecutionContext(7L, "MYSQL", "shop", null, "orders",
                "select * from orders", SqlExecutionOperation.EXPORT, "test"));

        assertEquals(List.of(1, 3), exporter.included(restrictedMetadata(), plan));
    }

    @Test
    void allFiveExportFormatsApplyPolicyOnceAndExcludeRestrictedColumns() throws Exception {
        String dbType = "EXPORT_EXTENSION_TEST";
        AtomicInteger beforeExecuteCalls = new AtomicInteger();
        IPlugin previousPlugin = Chat2DBContext.PLUGIN_MAP.put(dbType, plugin(dbType));
        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:export_extension;MODE=MySQL;DB_CLOSE_DELAY=-1")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE IF EXISTS orders");
                statement.execute("CREATE TABLE orders (id INT PRIMARY KEY, secret VARCHAR(64))");
                statement.execute("INSERT INTO orders (id, secret) VALUES (1, 'TOP_SECRET')");
            }
            ConnectInfo connectInfo = new ConnectInfo();
            connectInfo.setDataSourceId(7L);
            connectInfo.setDbType(dbType);
            connectInfo.setConnection(connection);
            connectInfo.setDriverConfig(new DriverConfig());
            Chat2DBContext.putContext(connectInfo);

            SqlExecutionPolicyManager policyManager = new SqlExecutionPolicyManager(List.of(
                    new ISqlExecutionPolicy() {
                        @Override
                        public void beforeExecute(SqlExecutionPlan plan) {
                            beforeExecuteCalls.incrementAndGet();
                        }

                        @Override
                        public boolean includeColumn(ai.chat2db.community.domain.api.model.sql.extension.SqlResultColumnContext context) {
                            return !"secret".equalsIgnoreCase(context.getColumnName());
                        }
                    }));
            ExportCellProcessorChain processorChain = new ExportCellProcessorChain(List.of());
            List<IExportStrategy> exporters = List.of(new CsvDataExporter(processorChain, policyManager),
                    new XlsDataExporter(processorChain, policyManager),
                    new XlsxDataExporter(processorChain, policyManager),
                    new JsonDataExporter(processorChain, policyManager),
                    new SqlDataExporter(processorChain, policyManager));

            int expectedBeforeExecuteCalls = 0;
            for (IExportStrategy exporter : exporters) {
                File output = tempDir.resolve("orders." + exporter.type()).toFile();
                ExportAsyncContext context = new ExportAsyncContext(null, null, output, exporter.type(),
                        List.of("orders"), "single", true);

                exporter.run(context);

                assertTrue(output.isFile(), exporter.type());
                assertRestrictedValueAbsent(output, exporter.type());
                assertEquals(++expectedBeforeExecuteCalls, beforeExecuteCalls.get(), exporter.type());
            }
        } finally {
            Chat2DBContext.removeContext();
            if (previousPlugin == null) {
                Chat2DBContext.PLUGIN_MAP.remove(dbType);
            } else {
                Chat2DBContext.PLUGIN_MAP.put(dbType, previousPlugin);
            }
        }
    }

    @Test
    void failedExportDeletesThePartialFileAndPropagatesTheFailure() {
        File output = tempDir.resolve("partial.test").toFile();
        ExportAsyncContext context = new ExportAsyncContext(null, null, output, "test", List.of("orders"),
                "single", true);

        assertThrows(IllegalStateException.class,
                () -> new FailingExporter().run(context));
        assertFalse(output.exists());
    }

    private static ExportCellContext cellContext() {
        return new ExportCellContext(7L, "MYSQL", "shop", null, "orders", "email", "csv");
    }

    private static ResultSetMetaData metadata() {
        return (ResultSetMetaData) Proxy.newProxyInstance(ExportExtensionTest.class.getClassLoader(),
                new Class<?>[]{ResultSetMetaData.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getColumnType" -> Types.VARCHAR;
                    case "getColumnTypeName" -> "VARCHAR";
                    case "getPrecision" -> 20;
                    case "getScale" -> 0;
                    case "getColumnName" -> "email";
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static ResultSetMetaData restrictedMetadata() {
        List<String> columns = List.of("id", "secret", "created_at");
        return (ResultSetMetaData) Proxy.newProxyInstance(ExportExtensionTest.class.getClassLoader(),
                new Class<?>[]{ResultSetMetaData.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getColumnCount" -> columns.size();
                    case "getColumnName", "getColumnLabel" -> columns.get((Integer) args[0] - 1);
                    case "getColumnType" -> Types.VARCHAR;
                    case "getColumnTypeName" -> "VARCHAR";
                    case "getTableName" -> "orders";
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static IPlugin plugin(String dbType) {
        DBConfig config = new DBConfig();
        config.setDbType(dbType);
        config.setDefaultDriverConfig(new DriverConfig());
        IDbMetaData metaData = new DefaultMetaService();
        return new IPlugin() {
            @Override
            public DBConfig getDBConfig() {
                return config;
            }

            @Override
            public IDbMetaData getDbMetaData() {
                return metaData;
            }
        };
    }

    private static void assertRestrictedValueAbsent(File output, String type) throws Exception {
        if ("xls".equals(type) || "xlsx".equals(type)) {
            try (Workbook workbook = WorkbookFactory.create(output)) {
                assertEquals(1, workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells(), type);
                assertEquals(1, workbook.getSheetAt(0).getRow(1).getPhysicalNumberOfCells(), type);
                assertFalse(workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue()
                        .equalsIgnoreCase("secret"), type);
            }
            return;
        }
        String content = Files.readString(output.toPath());
        assertFalse(content.contains("TOP_SECRET"), type);
        assertFalse(content.toLowerCase().contains("secret"), type);
        assertTrue(content.contains("1"), type);
    }

    private static JDBCDataValue jdbcValue(Object value) {
        ResultSet resultSet = (ResultSet) Proxy.newProxyInstance(ExportExtensionTest.class.getClassLoader(),
                new Class<?>[]{ResultSet.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getObject" -> value;
                    case "getString" -> value == null ? null : String.valueOf(value);
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        return new JDBCDataValue(resultSet, metadata(), 1, false);
    }

    private static IExportStrategy strategy(String type) {
        return new IExportStrategy() {
            @Override
            public String type() {
                return type;
            }

            @Override
            public void run(ExportAsyncContext asyncContext) {
            }
        };
    }

    private static SqlExecutionPolicyManager policyManager() {
        return new SqlExecutionPolicyManager(List.of());
    }

    private static final class TestExporter extends BaseExporter {

        private TestExporter(ExportCellProcessorChain chain) {
            this(chain, policyManager());
        }

        private TestExporter(ExportCellProcessorChain chain, SqlExecutionPolicyManager policyManager) {
            super(chain, policyManager);
        }

        private ExportCell process(ResultSetMetaData metadata, String tableName, Object value) throws Exception {
            return processCell(metadata, 1, tableName, value);
        }

        private ExportCell process(ResultSetMetaData metadata, String tableName, JDBCDataValue value)
                throws Exception {
            return processJdbcCell(metadata, 1, tableName, value);
        }

        private boolean next(ResultSet resultSet, SqlExecutionPlan plan, int exportedRowCount) throws Exception {
            return nextRow(resultSet, plan, exportedRowCount);
        }

        private List<Integer> included(ResultSetMetaData metadata, SqlExecutionPlan plan) throws Exception {
            return includedColumnIndexes(metadata, plan);
        }

        @Override
        public String type() {
            return "test";
        }

        @Override
        protected void singleExport(ExportAsyncContext asyncContext, String tableName, File file) {
        }
    }

    private static final class FailingExporter extends BaseExporter {

        private FailingExporter() {
            super(new ExportCellProcessorChain(List.of()), policyManager());
        }

        @Override
        public String type() {
            return "test";
        }

        @Override
        protected void singleExport(ExportAsyncContext asyncContext, String tableName, File file)
                throws IOException {
            Files.writeString(file.toPath(), "partial raw value");
            throw new IllegalStateException("serialization failed");
        }
    }
}
