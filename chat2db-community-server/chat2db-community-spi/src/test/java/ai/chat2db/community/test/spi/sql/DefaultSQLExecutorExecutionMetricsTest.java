package ai.chat2db.community.test.spi.sql;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.result.ExecutionMetrics;
import ai.chat2db.community.domain.api.model.result.ResultCell;
import ai.chat2db.community.domain.api.model.sql.SimpleSqlStatement;
import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;
import ai.chat2db.community.domain.api.service.db.ISqlExecutionResultConsumer;
import ai.chat2db.community.domain.api.service.db.ISqlExecutionStatementListener;
import ai.chat2db.community.tools.util.I18nUtils;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.model.request.SqlStatementExecuteRequest;
import ai.chat2db.spi.sql.Chat2DBContext;
import com.alibaba.druid.DbType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultSQLExecutorExecutionMetricsTest {

    private static final String TEST_DB_TYPE = "H2";
    private static final DefaultSQLExecutor EXECUTOR = new TestSQLExecutor();

    private IPlugin previousPlugin;

    @BeforeAll
    static void setUpI18n() throws Exception {
        Field field = I18nUtils.class.getDeclaredField("messageSourceStatic");
        field.setAccessible(true);
        field.set(null, new MessageSource() {
            @Override
            public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
                return defaultMessage == null ? code : defaultMessage;
            }

            @Override
            public String getMessage(String code, Object[] args, Locale locale) {
                return code;
            }

            @Override
            public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
                String[] codes = resolvable.getCodes();
                return codes == null || codes.length == 0 ? resolvable.getDefaultMessage() : codes[0];
            }
        });
    }

    @BeforeEach
    void setUpPlugin() {
        previousPlugin = Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, new TestPlugin());
    }

    @AfterEach
    void tearDownContext() {
        Chat2DBContext.removeContext();
        if (previousPlugin == null) {
            Chat2DBContext.PLUGIN_MAP.remove(TEST_DB_TYPE);
        } else {
            Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, previousPlugin);
        }
    }

    @Test
    void synchronousSelectAndUpdateExposeMetricsWithoutChangingResults() throws Exception {
        try (Connection connection = openDatabase("sync_metrics")) {
            putContext(connection);

            ExecuteResponse select = EXECUTOR.execute(SqlStatementExecuteRequest.builder()
                    .sql("SELECT * FROM sample ORDER BY id")
                    .connection(connection)
                    .limitRowSize(true)
                    .offset(0)
                    .count(10)
                    .build());

            assertEquals(2, select.getDataList().size());
            assertEquals(1, select.getStatementSequence());
            assertMetrics(select, 2);

            ExecuteResponse update = EXECUTOR.execute(SqlStatementExecuteRequest.builder()
                    .sql("UPDATE sample SET payload_text = 'updated' WHERE id = 1")
                    .connection(connection)
                    .limitRowSize(true)
                    .build());

            assertEquals(1, update.getUpdateCount());
            assertEquals(1, update.getStatementSequence());
            assertMetrics(update, 0);
        }
    }

    @Test
    void streamingResultsUseStatementOrderAndExposeFinalMetrics() throws Exception {
        try (Connection connection = openDatabase("stream_metrics")) {
            putContext(connection);
            SqlExecuteRequest request = request(
                    "SELECT 1; "
                            + "UPDATE sample SET payload_text = 'streamed' WHERE id = 2");
            CapturingConsumer consumer = new CapturingConsumer();

            EXECUTOR.executeStreaming(request, consumer, new NoOpStatementListener(),
                    () -> false);

            assertEquals(2, consumer.finishedResults.size());
            ExecuteResponse select = consumer.finishedResults.get(0);
            ExecuteResponse update = consumer.finishedResults.get(1);
            assertEquals(1, select.getStatementSequence());
            assertEquals(2, update.getStatementSequence());
            assertEquals(1, select.getDataList().size());
            assertEquals(1, update.getUpdateCount());
            assertMetrics(select, 1);
            assertMetrics(update, 0);
        }
    }

    @Test
    void failedStatementRetainsAvailableTimingInformation() throws Exception {
        try (Connection connection = openDatabase("failed_metrics")) {
            putContext(connection);
            SqlExecuteRequest request = request("UPDATE missing_table SET payload_text = 'missing'");
            request.setSingle(true);

            ExecuteResponse result = EXECUTOR.execute(request).get(0);

            assertEquals(Boolean.FALSE, result.getSuccess());
            assertEquals(1, result.getStatementSequence());
            assertNotNull(result.getDuration());
            assertMetrics(result, 0);
        }
    }

    @Test
    void statementDurationUsesLatestCumulativeResultDuration() {
        assertEquals(15L, TestSQLExecutor.statementDuration(List.of(
                ExecuteResponse.builder().duration(10L).build(),
                ExecuteResponse.builder().duration(15L).build())));
    }

    private static Connection openDatabase(String name) throws Exception {
        Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:" + name + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE sample (id INT PRIMARY KEY, payload_text VARCHAR(32))");
            statement.execute("INSERT INTO sample (id, payload_text) VALUES (1, 'one'), (2, 'two')");
        }
        return connection;
    }

    private static SqlExecuteRequest request(String script) {
        SqlExecuteRequest request = new SqlExecuteRequest();
        request.setScript(script);
        request.setConsoleId(1L);
        request.setDataSourceId(101L);
        request.setDatabaseName("");
        request.setSchemaName("PUBLIC");
        request.setPageNo(1);
        request.setPageSize(10);
        request.setErrorContinue(Boolean.TRUE);
        return request;
    }

    private static void assertMetrics(ExecuteResponse result, int expectedRowCount) {
        ExecutionMetrics metrics = result.getExecutionMetrics();
        assertNotNull(metrics);
        assertNotNull(metrics.getStartedAtEpochMs());
        assertNotNull(metrics.getFinishedAtEpochMs());
        assertTrue(metrics.getFinishedAtEpochMs() >= metrics.getStartedAtEpochMs());
        if (Boolean.TRUE.equals(result.getSuccess())) {
            assertEquals(expectedRowCount, metrics.getFetchedRowCount());
            assertNotNull(metrics.getExecuteDurationMs());
            assertNotNull(metrics.getFetchDurationMs());
            assertTrue(metrics.getExecuteDurationMs() >= 0L);
            assertTrue(metrics.getFetchDurationMs() >= 0L);
        } else {
            assertNull(metrics.getFetchedRowCount());
        }
    }

    private static void putContext(Connection connection) {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDataSourceId(101L);
        connectInfo.setDbType(TEST_DB_TYPE);
        connectInfo.setDatabaseName("");
        connectInfo.setSchemaName("PUBLIC");
        connectInfo.setConnection(connection);
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setDbType(TEST_DB_TYPE);
        connectInfo.setDriverConfig(driverConfig);
        Chat2DBContext.putContext(connectInfo);
    }

    private static final class CapturingConsumer implements ISqlExecutionResultConsumer {

        private final List<ExecuteResponse> finishedResults = new ArrayList<>();

        @Override
        public void statementStarted(String sql, String originalSql, String comment) {
        }

        @Override
        public void resultStarted(ExecuteResponse result) {
        }

        @Override
        public void rows(ExecuteResponse result, List<List<ResultCell>> rows) {
        }

        @Override
        public void resultFinished(ExecuteResponse result) {
            finishedResults.add(result);
        }

        @Override
        public void updateCount(ExecuteResponse result) {
        }

        @Override
        public void statementFinished(String sql, long duration) {
        }
    }

    private static final class NoOpStatementListener implements ISqlExecutionStatementListener {

        @Override
        public void onStatementCreated(Statement statement) {
        }

        @Override
        public void onStatementClosed(Statement statement) {
        }
    }

    private static final class TestSQLExecutor extends DefaultSQLExecutor {

        private static long statementDuration(List<ExecuteResponse> results) {
            return maximumStatementDuration(results);
        }

        @Override
        protected List<SimpleSqlStatement> buildSimpleSqlStatements(SqlExecuteRequest command, DbType dbType,
                                                                     String type, DBConfig dbConfig) {
            return Arrays.stream(command.getScript().split(";"))
                    .map(String::trim)
                    .filter(sql -> !sql.isEmpty())
                    .map(SimpleSqlStatement::new)
                    .toList();
        }
    }

    private static final class TestPlugin implements IPlugin {

        private final DBConfig dbConfig;
        private final IDbMetaData metaData = new DefaultMetaService();

        private TestPlugin() {
            dbConfig = new DBConfig();
            dbConfig.setDbType(TEST_DB_TYPE);
        }

        @Override
        public DBConfig getDBConfig() {
            return dbConfig;
        }

        @Override
        public IDbMetaData getDbMetaData() {
            return metaData;
        }
    }
}
