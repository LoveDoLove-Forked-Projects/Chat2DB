package ai.chat2db.plugin.mysql.completion.plan;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import java.util.List;
import org.antlr.v4.runtime.Token;


public final class MysqlSqlCompletionContextGuards {

    private MysqlSqlCompletionContextGuards() {
    }

    public static boolean invalidFixedAggregateExtraArgument(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return false;
        }
        List<Token> tokens = MysqlSqlCompletionTokenUtil.defaultTokens(context.window().parseSql());
        int cursor = Math.max(0, Math.min(context.cursorContext().replaceStart(), context.window().parseSql().length()));
        return MysqlSqlCompletionTokenUtil.isInvalidFixedAggregateExtraArgument(tokens, cursor);
    }
}
