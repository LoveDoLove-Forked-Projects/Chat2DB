package ai.chat2db.plugin.postgresql.parser.error.strategy;

import ai.chat2db.spi.parser.error.strategy.BaseErrorStrategy;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParser;
import org.antlr.v4.runtime.misc.IntervalSet;

public class PgsqlErrorStrategy extends BaseErrorStrategy {

    private static final int[] stmtBeginToken = new int[]{
            PostgreSQLParser.SEMI, PostgreSQLParser.SELECT, PostgreSQLParser.INSERT,
            PostgreSQLParser.UPDATE, PostgreSQLParser.DELETE_P, PostgreSQLParser.ALTER,
            PostgreSQLParser.ANALYZE,PostgreSQLParser.ANALYSE,PostgreSQLParser.CALL,
            PostgreSQLParser.CHECKPOINT,PostgreSQLParser.CLOSE,PostgreSQLParser.CLUSTER,
            PostgreSQLParser.COMMENT, PostgreSQLParser.COPY,
            PostgreSQLParser.CREATE,PostgreSQLParser.DEALLOCATE,PostgreSQLParser.DECLARE,
            PostgreSQLParser.WITH,PostgreSQLParser.DISCARD,PostgreSQLParser.DO,
            PostgreSQLParser.DROP,PostgreSQLParser.EXPLAIN,PostgreSQLParser.FETCH,
            PostgreSQLParser.EXECUTE,PostgreSQLParser.MOVE,PostgreSQLParser.GRANT,
            PostgreSQLParser.IMPORT_P,PostgreSQLParser.MERGE,PostgreSQLParser.LISTEN,
            PostgreSQLParser.REFRESH,PostgreSQLParser.LOAD,PostgreSQLParser.LOCK_P,
            PostgreSQLParser.NOTIFY,PostgreSQLParser.REINDEX,PostgreSQLParser.PREPARE,
            PostgreSQLParser.REASSIGN,PostgreSQLParser.REVOKE,PostgreSQLParser.SECURITY,
            PostgreSQLParser.ABORT_P,PostgreSQLParser.BEGIN_P,PostgreSQLParser.START,
            PostgreSQLParser.COMMIT,PostgreSQLParser.END_P,PostgreSQLParser.ROLLBACK,
            PostgreSQLParser.SAVEPOINT,PostgreSQLParser.RELEASE,PostgreSQLParser.TRUNCATE,
            PostgreSQLParser.UNLISTEN,PostgreSQLParser.VACUUM,PostgreSQLParser.RESET,
            PostgreSQLParser.MetaCommand
    };

    public static final IntervalSet recoverSet = new IntervalSet(stmtBeginToken);

}
