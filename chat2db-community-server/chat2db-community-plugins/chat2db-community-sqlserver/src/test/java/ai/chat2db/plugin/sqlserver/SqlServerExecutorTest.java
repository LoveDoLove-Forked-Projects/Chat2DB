package ai.chat2db.plugin.sqlserver;

import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
