package ai.chat2db.plugin.sqlserver.parser.error.strategy;

import ai.chat2db.spi.parser.error.strategy.BaseErrorStrategy;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParser;
import org.antlr.v4.runtime.misc.IntervalSet;

public class SqlServerErrorStrategy extends BaseErrorStrategy {

    private static final int[] stmtBeginToken = new int[]{
            TSqlParser.GO, TSqlParser.CREATE,TSqlParser.ALTER,
            TSqlParser.SEMI,TSqlParser.WITH,TSqlParser.MERGE,
            TSqlParser.DELETE,TSqlParser.INSERT,TSqlParser.SELECT,
            TSqlParser.UPDATE,TSqlParser.BEGIN,TSqlParser.PRINT,
            TSqlParser.CHECKPOINT,TSqlParser.END,TSqlParser.GET,
            TSqlParser.SEND,TSqlParser.WAITFOR,TSqlParser.CLOSE,
            TSqlParser.DEALLOCATE,TSqlParser.DECLARE,TSqlParser.FETCH,
            TSqlParser.OPEN,TSqlParser.DECLARE,TSqlParser.WITH,
            TSqlParser.EXECUTE,TSqlParser.KILL,TSqlParser.RECONFIGURE,
            TSqlParser.GRANT, TSqlParser.SETUSER,
            TSqlParser.SHUTDOWN,TSqlParser.COMMIT,TSqlParser.SAVE,
            TSqlParser.ROLLBACK,TSqlParser.USE,TSqlParser.DISABLE,
            TSqlParser.DROP,TSqlParser.ENABLE,TSqlParser.LOCK,
            TSqlParser.TRUNCATE,TSqlParser.DBCC,TSqlParser.BACKUP,


    };
    public static final IntervalSet recoverSet = new IntervalSet(stmtBeginToken);


}
