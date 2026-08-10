package ai.chat2db.community.domain.core.impl.task.imports.json;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.task.ImportAsyncContext;
import ai.chat2db.community.domain.api.service.task.ITaskImportSqlExecutor;
import ai.chat2db.community.tools.model.Context;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JSONImporterExtensionTest {

    private static final String TEST_DB_TYPE = "JSON_IMPORT_POLICY_TEST";

    private IPlugin previousPlugin;

    @BeforeEach
    void setUp() {
        previousPlugin = Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, plugin());
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDataSourceId(7L);
        connectInfo.setDbType(TEST_DB_TYPE);
        connectInfo.setDatabaseName("shop");
        connectInfo.setDriverConfig(new DriverConfig());
        Chat2DBContext.putContext(connectInfo);
    }

    @AfterEach
    void tearDown() {
        Chat2DBContext.removeContext();
        if (previousPlugin == null) {
            Chat2DBContext.PLUGIN_MAP.remove(TEST_DB_TYPE);
        } else {
            Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, previousPlugin);
        }
    }

    @Test
    void jsonImportDelegatesGeneratedStatementsToTaskSqlExecutor(@TempDir Path directory) throws Exception {
        Path input = directory.resolve("orders.json");
        Files.writeString(input, "[{\"id\":1},{\"id\":2}]");
        List<String> executedSql = new ArrayList<>();
        ImportAsyncContext context = new ImportAsyncContext(update -> {
        }, new Context(), "json", "orders", input.toFile());
        context.setSqlExecutor(new ITaskImportSqlExecutor() {
            @Override
            public String executeBatch(int batch, List<String> sqls) {
                executedSql.addAll(sqls);
                return "success";
            }

            @Override
            public String executeSql(int batch, String sql) {
                executedSql.add(sql);
                return "success";
            }
        });
        TableColumn id = TableColumn.builder().name("id").columnType("INTEGER").build();

        new JSONImporter().doImportData(context, List.of(id));

        assertEquals(2, executedSql.size());
        assertTrue(executedSql.stream().allMatch(sql -> sql.toLowerCase().contains("insert into")));
        assertTrue(executedSql.stream().allMatch(sql -> sql.contains("orders")));
    }

    private IPlugin plugin() {
        DBConfig config = new DBConfig();
        config.setDbType(TEST_DB_TYPE);
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
}
