package ai.chat2db.plugin.mysql.parser.error.strategy;

import ai.chat2db.spi.parser.error.strategy.BaseErrorStrategy;
import ai.chat2db.mysql.parser.base.MySqlParser;
import org.antlr.v4.runtime.misc.IntervalSet;

public class MysqlErrorStrategy extends BaseErrorStrategy {

    private static final int[] stmtBeginToken = new int[]{
            MySqlParser.SELECT, MySqlParser.INSERT, MySqlParser.UPDATE,
            MySqlParser.DELETE, MySqlParser.ALTER, MySqlParser.DROP,
            MySqlParser.COMMIT, MySqlParser.CREATE, MySqlParser.GRANT,
            MySqlParser.RENAME, MySqlParser.REVOKE, MySqlParser.COMMENT,
            MySqlParser.EXPLAIN, MySqlParser.ROLLBACK, MySqlParser.WITH,
              MySqlParser.SEMI, MySqlParser.TRUNCATE,
            MySqlParser.REPLACE, MySqlParser.CALL, MySqlParser.LOAD,
            MySqlParser.DO, MySqlParser.HANDLER, MySqlParser.GET,
            MySqlParser.START, MySqlParser.BEGIN, MySqlParser.SAVEPOINT,
            MySqlParser.RELEASE, MySqlParser.LOCK, MySqlParser.UNLOCK,
            MySqlParser.PURGE, MySqlParser.KILL, MySqlParser.CHANGE,
            MySqlParser.RESET, MySqlParser.SHOW, MySqlParser.DESCRIBE,
            MySqlParser.OPTIMIZE, MySqlParser.REPAIR, MySqlParser.CHECK,
            MySqlParser.STOP, MySqlParser.INSTALL, MySqlParser.UNINSTALL,
            MySqlParser.FLUSH, MySqlParser.DESC, MySqlParser.USE,
            MySqlParser.HELP, MySqlParser.SIGNAL, MySqlParser.RESIGNAL,
            MySqlParser.BINLOG, MySqlParser.CACHE, MySqlParser.ANALYZE,
            MySqlParser.EXECUTE, MySqlParser.PREPARE, MySqlParser.DEALLOCATE
    };

    public static final IntervalSet recoverSet = new IntervalSet(stmtBeginToken);


}
