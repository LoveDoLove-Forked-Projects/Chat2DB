package ai.chat2db.plugin.sqlserver;

import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.spi.model.request.SqlStatementExecuteRequest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlServerExecutorTest {

    @Test
    void shouldKeepGoBatchWhenExecutingFormalSql() {
        SqlServerExecutor executor = new SqlServerExecutor();
        SqlExecuteRequest command = new SqlExecuteRequest();
        command.setScript("SET SHOWPLAN_XML ON;\nGO\nSELECT * FROM uf_wtbhb WHERE lcid=1208045;\nGO\nSET SHOWPLAN_XML OFF;");
        command.setExplain(false);

        executor.prepareCommandScript(command);

        assertEquals("SET SHOWPLAN_XML ON;\nGO\nSELECT * FROM uf_wtbhb WHERE lcid=1208045;\nGO\nSET SHOWPLAN_XML OFF;",
                command.getScript());
    }

    @Test
    void shouldSplitSqlServerBatchByGoDelimiter() {
        SqlServerExecutor executor = new SqlServerExecutor();

        List<String> sqlList = executor.splitByGO(
                "SET SHOWPLAN_XML ON;\nGO\nSELECT * FROM uf_wtbhb WHERE lcid=1208045;\nGO\nSET SHOWPLAN_XML OFF;");

        assertEquals(List.of(
                "SET SHOWPLAN_XML ON;",
                "SELECT * FROM uf_wtbhb WHERE lcid=1208045;",
                "SET SHOWPLAN_XML OFF;"), sqlList);
    }

    @Test
    void shouldSplitInlineGoAfterStatementTerminator() {
        SqlServerExecutor executor = new SqlServerExecutor();

        List<String> sqlList = executor.splitByGO(
                "SET SHOWPLAN_XML ON;\nGO\nSELECT * FROM uf_wtbhb WHERE lcid=1208045;GO\nSET SHOWPLAN_XML OFF;");

        assertEquals(List.of(
                "SET SHOWPLAN_XML ON;",
                "SELECT * FROM uf_wtbhb WHERE lcid=1208045;",
                "SET SHOWPLAN_XML OFF;"), sqlList);
    }

    @Test
    void shouldExposeMetricsForGoBatch() throws Exception {
        SqlServerExecutor executor = new SqlServerExecutor();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:sqlserver_go_metrics")) {
            ExecuteResponse result = executor.execute(SqlStatementExecuteRequest.builder()
                    .sql("CREATE TABLE sample (id INT PRIMARY KEY);\nGO\n"
                            + "INSERT INTO sample (id) VALUES (1);\nGO\n"
                            + "UPDATE sample SET id = 2 WHERE id = 1;")
                    .connection(connection)
                    .limitRowSize(true)
                    .offset(0)
                    .count(10)
                    .build());

            assertEquals(1, result.getUpdateCount());
            assertEquals(1, result.getStatementSequence());
            assertNotNull(result.getExecutionMetrics());
            assertNotNull(result.getExecutionMetrics().getStartedAtEpochMs());
            assertNotNull(result.getExecutionMetrics().getFinishedAtEpochMs());
            assertTrue(result.getExecutionMetrics().getExecuteDurationMs() >= 0L);
            assertEquals(0L, result.getExecutionMetrics().getFetchDurationMs());
            assertEquals(0, result.getExecutionMetrics().getFetchedRowCount());
        }
    }
}
