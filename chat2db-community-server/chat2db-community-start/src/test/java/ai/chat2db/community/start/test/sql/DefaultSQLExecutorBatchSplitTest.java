package ai.chat2db.community.start.test.sql;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;
import ai.chat2db.community.domain.api.model.sql.SimpleSqlStatement;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import com.alibaba.druid.DbType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultSQLExecutorBatchSplitTest {

    private static final String SQL_SERVER_BATCH = "DECLARE @RecordId INT =1208045;\n"
            + "SELECT * FROM uf_wtbhb WHERE lcid=@RecordId;";

    private final TestDefaultSQLExecutor executor = new TestDefaultSQLExecutor();

    @BeforeEach
    void setUp() {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDbType(DatabaseTypeEnum.SQLSERVER.name());
        Chat2DBContext.putContext(connectInfo);
    }

    @AfterEach
    void tearDown() {
        Chat2DBContext.removeContext();
    }

    @Test
    void shouldKeepSplitForDatabasesWithoutBatchCapability() {
        SqlExecuteRequest command = buildCommand(false);
        DBConfig dbConfig = new DBConfig();

        List<SimpleSqlStatement> statements =
                executor.build(command, DbType.sqlserver, "SQLSERVER", dbConfig);

        assertEquals(2, statements.size());
    }

    @Test
    void shouldPreserveWholeBatchWhenDatabaseSupportsIt() {
        SqlExecuteRequest command = buildCommand(false);
        DBConfig dbConfig = new DBConfig();
        dbConfig.setPreserveScriptBatchExecution(true);

        List<SimpleSqlStatement> statements =
                executor.build(command, DbType.sqlserver, "SQLSERVER", dbConfig);

        assertEquals(1, statements.size());
        assertEquals(SQL_SERVER_BATCH, statements.get(0).getSql());
    }

    @Test
    void shouldKeepSingleModeBehavior() {
        SqlExecuteRequest command = buildCommand(true);
        DBConfig dbConfig = new DBConfig();

        List<SimpleSqlStatement> statements =
                executor.build(command, DbType.sqlserver, "SQLSERVER", dbConfig);

        assertEquals(1, statements.size());
        assertEquals(SQL_SERVER_BATCH.trim(), statements.get(0).getSql());
    }

    private SqlExecuteRequest buildCommand(boolean single) {
        SqlExecuteRequest command = new SqlExecuteRequest();
        command.setScript(SQL_SERVER_BATCH);
        command.setSingle(single);
        command.setExplain(false);
        return command;
    }

    private static class TestDefaultSQLExecutor extends DefaultSQLExecutor {
        List<SimpleSqlStatement> build(SqlExecuteRequest command, DbType dbType, String type, DBConfig dbConfig) {
            return buildSimpleSqlStatements(command, dbType, type, dbConfig);
        }
    }
}
