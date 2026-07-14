package ai.chat2db.community.test.spi.sql;

import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;
import ai.chat2db.community.domain.api.model.sql.SimpleSqlStatement;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.util.SqlUtils;
import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultSQLExecutorMultilineStringTest {

    private static final String MULTILINE_UPDATE_SQL = "UPDATE ai_chat_message set `content` = 'ins\n\ns\nda\nsd\nas"
            + "\ndaas\ndaasdasd\nasda\nsda\nsda' where `id` = '1409bc33-0143-4aff-b0d8-d513165c7e33'  LIMIT 1;\n";

    @Test
    void mysqlSplitterKeepsRawNewlinesInsideStringLiteral() {
        List<String> statements = SqlUtils.parse(MULTILINE_UPDATE_SQL, DbType.mysql, true);

        assertEquals(1, statements.size());
        assertTrue(statements.get(0).contains("'ins\n\ns\nda\nsd"));
        assertTrue(statements.get(0).contains("where `id` = '1409bc33-0143-4aff-b0d8-d513165c7e33'"));
        assertTrue(statements.get(0).contains("LIMIT 1"));
    }

    @Test
    void generalExecutorAnalysisKeepsRawNewlinesInsideStringLiteral() {
        putMysqlContext();
        try {
            SqlExecuteRequest command = new SqlExecuteRequest();
            command.setScript(MULTILINE_UPDATE_SQL);

            List<SimpleSqlStatement> statements = new TestDefaultSQLExecutor().build(command);

            assertEquals(1, statements.size());
            assertTrue(statements.get(0).getSql().contains("'ins\n\ns\nda\nsd"));
            assertTrue(statements.get(0).getSql().contains("LIMIT 1"));
        } finally {
            Chat2DBContext.removeContext();
        }
    }

    private static void putMysqlContext() {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDbType("MYSQL");
        connectInfo.setDataSourceId(46L);
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setDbType("MYSQL");
        connectInfo.setDriverConfig(driverConfig);
        Chat2DBContext.putContext(connectInfo);
    }

    private static class TestDefaultSQLExecutor extends DefaultSQLExecutor {
        private List<SimpleSqlStatement> build(SqlExecuteRequest command) {
            return buildSimpleSqlStatements(command, DbType.mysql, "MYSQL", null);
        }
    }
}
