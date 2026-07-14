package ai.chat2db.plugin.mysql.completion.provider.filter;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import java.util.List;
import java.util.Objects;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionCompletedIdentifierFilter {

    private MysqlSqlCompletionCompletedIdentifierFilter() {
    }

    public static boolean repeatsCompletedIdentifier(MysqlSqlCompletionCandidateContext context, String candidateName) {
        if (StringUtils.isBlank(candidateName)) {
            return false;
        }
        return hasCompletedIdentifierMatch(context, List.of(candidateName));
    }

    public static boolean hasCompletedIdentifierMatch(MysqlSqlCompletionCandidateContext context,
                                                      List<String> candidateNames) {
        if (context == null || context.window() == null || context.cursorContext() == null
                || candidateNames == null || candidateNames.isEmpty() || StringUtils.isBlank(context.prefix())) {
            return false;
        }
        List<Token> tokens = MysqlSqlCompletionTokenUtil.defaultTokens(context.window().parseSql());
        if (!MysqlSqlCompletionTokenUtil.isAtIdentifierTokenEnd(tokens, context.cursorContext().replaceStart(),
                context.cursorContext().replaceEnd(), context.prefix())) {
            return false;
        }
        String prefix = MysqlSqlCompletionTokenUtil.stripQuote(context.prefix());
        return candidateNames.stream()
                .filter(Objects::nonNull)
                .anyMatch(candidateName -> StringUtils.equalsIgnoreCase(
                        MysqlSqlCompletionTokenUtil.stripQuote(candidateName), prefix));
    }
}
