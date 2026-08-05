package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.enums.plugin.ResultSetEditorTypeEnum;
import ai.chat2db.community.domain.api.model.metadata.PrimaryKey;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.request.db.DbExecuteResultEnhanceRequest;
import ai.chat2db.community.domain.api.model.request.db.DbTableQueryRequest;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.ResultSetEditorMetadata;
import ai.chat2db.community.domain.api.model.result.ResultSetEditorOption;
import ai.chat2db.community.domain.api.service.db.IDbTableService;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.model.request.TableMetadataRequest;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecuteResultHeaderEnhancerTest {

    private static final String TEST_DB_TYPE = "RESULT_HEADER_ENHANCER_TEST";

    private Connection connection;
    private IPlugin previousPlugin;

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:result_header_enhancer;DB_CLOSE_DELAY=-1");
    }

    @AfterEach
    void tearDown() throws Exception {
        Chat2DBContext.removeContext();
        if (previousPlugin == null) {
            Chat2DBContext.PLUGIN_MAP.remove(TEST_DB_TYPE);
        } else {
            Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, previousPlugin);
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void queriesBatchOnceAndMapsAliasedHeaderMetadataByColumnIdentity() {
        AtomicInteger columnQueries = new AtomicInteger();
        AtomicReference<DbTableQueryRequest> capturedRequest = new AtomicReference<>();
        TableColumn statusColumn = TableColumn.builder()
                .name("status")
                .columnType("ENUM")
                .comment("workflow status")
                .build();
        TableColumn equalStatusColumn = TableColumn.builder()
                .name("status")
                .columnType("ENUM")
                .comment("workflow status")
                .build();
        assertEquals(statusColumn, equalStatusColumn);
        IDbTableService tableService = tableService(List.of(statusColumn, equalStatusColumn), columnQueries,
                capturedRequest);
        TestMetaData metaData = new TestMetaData(columns -> {
            assertSame(statusColumn, columns.get(0));
            assertSame(equalStatusColumn, columns.get(1));
            return Map.of(
                    0, ResultSetEditorMetadata.builder()
                            .editorType(ResultSetEditorTypeEnum.SELECT.getCode())
                            .editorOptions(List.of(
                                    ResultSetEditorOption.builder().label("PENDING").value("PENDING").build(),
                                    ResultSetEditorOption.builder().label("DONE").value("DONE").build()))
                            .build(),
                    1, ResultSetEditorMetadata.builder()
                            .editorType(ResultSetEditorTypeEnum.DATETIME.getCode())
                            .editorOptions(List.of())
                            .build());
        });
        putContext(metaData);

        Header header = Header.builder()
                .name("status_alias")
                .columnName("status")
                .editorType(ResultSetEditorTypeEnum.TEXT.getCode())
                .build();
        ExecuteResponse response = editableResponse(header);
        enhance(tableService, response);

        assertEquals(1, columnQueries.get());
        assertEquals(1, metaData.getBatchResolverCalls());
        assertTrue(capturedRequest.get().isRefresh());
        assertEquals("orders", capturedRequest.get().getTableName());
        assertEquals("workflow status", header.getComment());
        assertEquals(ResultSetEditorTypeEnum.SELECT.getCode(), header.getEditorType());
        assertEquals(List.of("PENDING", "DONE"),
                header.getEditorOptions().stream().map(ResultSetEditorOption::getValue).toList());
    }

    @Test
    void missingBatchEntryKeepsExistingEditorAndDoesNotStopOtherColumns() {
        AtomicInteger columnQueries = new AtomicInteger();
        TableColumn brokenColumn = TableColumn.builder().name("broken").columnType("ENUM").build();
        TableColumn createdAtColumn = TableColumn.builder().name("created_at").columnType("DATETIME").build();
        IDbTableService tableService = tableService(List.of(brokenColumn, createdAtColumn), columnQueries,
                new AtomicReference<>());
        TestMetaData metaData = new TestMetaData(columns -> Map.of(1, ResultSetEditorMetadata.builder()
                    .editorType(ResultSetEditorTypeEnum.DATETIME.getCode())
                    .editorOptions(List.of())
                    .build()));
        putContext(metaData);

        Header brokenHeader = Header.builder()
                .name("broken")
                .columnName("broken")
                .editorType(ResultSetEditorTypeEnum.TEXT.getCode())
                .build();
        Header createdAtHeader = Header.builder()
                .name("created_at")
                .columnName("created_at")
                .editorType(ResultSetEditorTypeEnum.TEXT.getCode())
                .build();
        ExecuteResponse response = editableResponse(brokenHeader, createdAtHeader);
        enhance(tableService, response);

        assertEquals(1, columnQueries.get());
        assertEquals(1, metaData.getBatchResolverCalls());
        assertEquals(ResultSetEditorTypeEnum.TEXT.getCode(), brokenHeader.getEditorType());
        assertNull(brokenHeader.getEditorOptions());
        assertEquals(ResultSetEditorTypeEnum.DATETIME.getCode(), createdAtHeader.getEditorType());
        assertNull(createdAtHeader.getEditorOptions());
    }

    @Test
    void defaultCapabilityKeepsLegacyEditorFallbackAndSkipsStructuredResolver() {
        AtomicInteger structuredResolverCalls = new AtomicInteger();
        TableColumn column = TableColumn.builder().name("created_at").columnType("DATETIME").build();
        IDbTableService tableService = tableService(List.of(column), new AtomicInteger(), new AtomicReference<>());
        TestMetaData metaData = new TestMetaData(columns -> {
            structuredResolverCalls.incrementAndGet();
            return Map.of(0, ResultSetEditorMetadata.builder()
                            .editorType(ResultSetEditorTypeEnum.SELECT.getCode())
                            .editorOptions(List.of(new ResultSetEditorOption("unexpected", "unexpected")))
                            .build());
        }, false, ResultSetEditorTypeEnum.DATETIME.getCode());
        putContext(metaData);

        Header header = Header.builder()
                .name("created_at")
                .columnName("created_at")
                .editorType(ResultSetEditorTypeEnum.TEXT.getCode())
                .build();
        enhance(tableService, editableResponse(header));

        assertEquals(0, structuredResolverCalls.get());
        assertEquals(0, metaData.getBatchResolverCalls());
        assertEquals(ResultSetEditorTypeEnum.DATETIME.getCode(), header.getEditorType());
        assertNull(header.getEditorOptions());
    }

    @Test
    void defaultBatchResolverDelegatesToSingleColumnHookAndIsolatesFailures() {
        AtomicInteger resolverCalls = new AtomicInteger();
        IDbMetaData metaData = new SingleColumnMetaData(resolverCalls);
        TableColumn brokenColumn = TableColumn.builder().name("broken").columnType("ENUM").build();
        TableColumn statusColumn = TableColumn.builder().name("status").columnType("ENUM").build();

        Map<Integer, ResultSetEditorMetadata> metadataByIndex = metaData.resolveResultSetEditorMetadata(
                connection, List.of(brokenColumn, statusColumn));

        assertEquals(2, resolverCalls.get());
        assertFalse(metadataByIndex.containsKey(0));
        assertEquals(ResultSetEditorTypeEnum.SELECT.getCode(), metadataByIndex.get(1).getEditorType());
        assertEquals(List.of("OPEN", "CLOSED"),
                metadataByIndex.get(1).getEditorOptions().stream().map(ResultSetEditorOption::getValue).toList());
    }

    private void putContext(IDbMetaData metaData) {
        previousPlugin = Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, new TestPlugin(metaData));
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDataSourceId(17L);
        connectInfo.setDbType(TEST_DB_TYPE);
        connectInfo.setDatabaseName("catalog");
        connectInfo.setSchemaName("schema");
        connectInfo.setConnection(connection);
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setDbType(TEST_DB_TYPE);
        connectInfo.setDriverConfig(driverConfig);
        Chat2DBContext.putContext(connectInfo);
    }

    private IDbTableService tableService(List<TableColumn> columns, AtomicInteger queryCount,
                                         AtomicReference<DbTableQueryRequest> capturedRequest) {
        return (IDbTableService) Proxy.newProxyInstance(
                IDbTableService.class.getClassLoader(),
                new Class<?>[]{IDbTableService.class},
                (proxy, method, args) -> {
                    if ("queryColumns".equals(method.getName())) {
                        queryCount.incrementAndGet();
                        capturedRequest.set((DbTableQueryRequest) args[0]);
                        return columns;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    private ExecuteResponse editableResponse(Header... headers) {
        return ExecuteResponse.builder()
                .success(true)
                .canEdit(true)
                .tableName("orders")
                .headerList(List.of(headers))
                .build();
    }

    private void enhance(IDbTableService tableService, ExecuteResponse response) {
        DbExecuteResultEnhanceRequest request = new DbExecuteResultEnhanceRequest();
        request.setExecuteResult(response);
        new ExecuteResultHeaderEnhancer(tableService).enhance(request);
    }

    private static final class TestMetaData extends DefaultMetaService {

        private final Function<List<TableColumn>, Map<Integer, ResultSetEditorMetadata>> batchResolver;
        private final AtomicInteger batchResolverCalls = new AtomicInteger();
        private final boolean supportsOptions;
        private final String legacyEditorType;

        private TestMetaData(Function<List<TableColumn>, Map<Integer, ResultSetEditorMetadata>> batchResolver) {
            this(batchResolver, true, ResultSetEditorTypeEnum.TEXT.getCode());
        }

        private TestMetaData(Function<List<TableColumn>, Map<Integer, ResultSetEditorMetadata>> batchResolver,
                             boolean supportsOptions,
                             String legacyEditorType) {
            this.batchResolver = batchResolver;
            this.supportsOptions = supportsOptions;
            this.legacyEditorType = legacyEditorType;
        }

        @Override
        public List<PrimaryKey> getPrimaryKeys(Connection connection, TableMetadataRequest tableMetadataRequest) {
            return List.of();
        }

        @Override
        public Map<Integer, ResultSetEditorMetadata> resolveResultSetEditorMetadata(
                Connection connection, List<TableColumn> columns) {
            batchResolverCalls.incrementAndGet();
            return batchResolver.apply(columns);
        }

        @Override
        public ResultSetEditorMetadata resolveResultSetEditorMetadata(TableColumn column) {
            throw new AssertionError("Runtime must use the batched result-set editor metadata hook");
        }

        @Override
        public boolean supportsResultSetEditorOptions() {
            return supportsOptions;
        }

        @Override
        public String resolveResultSetEditorType(String typeName, Integer type) {
            return legacyEditorType;
        }

        private int getBatchResolverCalls() {
            return batchResolverCalls.get();
        }
    }

    private static final class SingleColumnMetaData extends DefaultMetaService {

        private final AtomicInteger resolverCalls;

        private SingleColumnMetaData(AtomicInteger resolverCalls) {
            this.resolverCalls = resolverCalls;
        }

        @Override
        public List<PrimaryKey> getPrimaryKeys(Connection connection, TableMetadataRequest tableMetadataRequest) {
            return List.of();
        }

        @Override
        public ResultSetEditorMetadata resolveResultSetEditorMetadata(TableColumn column) {
            resolverCalls.incrementAndGet();
            if ("broken".equals(column.getName())) {
                throw new IllegalArgumentException("malformed metadata");
            }
            return ResultSetEditorMetadata.builder()
                    .editorType(ResultSetEditorTypeEnum.SELECT.getCode())
                    .editorOptions(List.of(
                            new ResultSetEditorOption("OPEN", "OPEN"),
                            new ResultSetEditorOption("CLOSED", "CLOSED")))
                    .build();
        }

        @Override
        public boolean supportsResultSetEditorOptions() {
            return true;
        }
    }

    private static final class TestPlugin implements IPlugin {

        private final IDbMetaData metaData;

        private TestPlugin(IDbMetaData metaData) {
            this.metaData = metaData;
        }

        @Override
        public DBConfig getDBConfig() {
            DBConfig dbConfig = new DBConfig();
            dbConfig.setDbType(TEST_DB_TYPE);
            return dbConfig;
        }

        @Override
        public IDbMetaData getDbMetaData() {
            return metaData;
        }
    }
}
