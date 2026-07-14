package ai.chat2db.plugin.oracle.parser.error.strategy;

import ai.chat2db.spi.parser.error.strategy.BaseErrorStrategy;
import ai.chat2db.plugin.oracle.parser.base.PlSqlParser;
import org.antlr.v4.runtime.misc.IntervalSet;

public class OracleErrorStrategy extends BaseErrorStrategy {

    private static final int[] stmtBeginToken = new int[]{
            PlSqlParser.SELECT, PlSqlParser.INSERT, PlSqlParser.UPDATE,
            PlSqlParser.DELETE, PlSqlParser.ALTER, PlSqlParser.DROP,
            PlSqlParser.ADMINISTER, PlSqlParser.ANALYZE, PlSqlParser.BEGIN,
            PlSqlParser.COMMIT, PlSqlParser.CREATE, PlSqlParser.DECLARE,
            PlSqlParser.EXECUTE, PlSqlParser.GRANT, PlSqlParser.RENAME,
            PlSqlParser.REVOKE, PlSqlParser.SAVEPOINT, PlSqlParser.TRUNCATE,
            PlSqlParser.LOCK, PlSqlParser.NOAUDIT, PlSqlParser.PURGE,
            PlSqlParser.EXPLAIN, PlSqlParser.FLASHBACK, PlSqlParser.ASSOCIATE,
            PlSqlParser.AUDIT, PlSqlParser.MERGE, PlSqlParser.ROLLBACK,
            PlSqlParser.DISASSOCIATE,  PlSqlParser.SEMICOLON,
            PlSqlParser.COMMENT,PlSqlParser.WITH,PlSqlParser.EXIT,
            PlSqlParser.PROMPT_MESSAGE,PlSqlParser.SHOW,PlSqlParser.WHENEVER,
            PlSqlParser.TIMING,PlSqlParser.START_CMD,

    };

    public static final IntervalSet recoverSet = new IntervalSet(stmtBeginToken);


}
