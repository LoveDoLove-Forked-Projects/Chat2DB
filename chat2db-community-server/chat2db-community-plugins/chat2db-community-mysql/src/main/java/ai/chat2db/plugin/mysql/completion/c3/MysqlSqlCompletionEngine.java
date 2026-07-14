package ai.chat2db.plugin.mysql.completion.c3;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.spi.parser.completion.CodeCompletionCore;
import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;


public final class MysqlSqlCompletionEngine {

    private MysqlSqlCompletionEngine() {
    }

    public static SqlCompletionCandidates collect(MysqlSqlCompletionCandidateContext context) {
        return collect(context, Set.of());
    }

    public static SqlCompletionCandidates collect(MysqlSqlCompletionCandidateContext context,
                                                  Set<Integer> additionalPreferredRules) {
        if (context == null || context.dummySql() == null || context.cursorContext() == null
                || !context.cursorContext().admitted()) {
            return SqlCompletionCandidates.unavailable();
        }
        String sql = context.dummySql().sql();
        if (sql == null || sql.isBlank()) {
            return SqlCompletionCandidates.unavailable();
        }
        String parseSql = sql.stripTrailing().endsWith(";") ? sql : sql + ";";
        MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(parseSql));
        lexer.removeErrorListeners();
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        int tokenIndex = tokenIndexAt(tokenStream, context);
        tokenStream.fill();
        MySqlParser parser = new MySqlParser(tokenStream);
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());
        if (tokenIndex < 0) {
            tokenIndex = 0;
        }
        try {
            CodeCompletionCore core = new CodeCompletionCore(parser);
            core.ignoredTokens = new HashSet<>(MysqlSqlCompletionConfig.ignoredTokens());
            core.preferredRules = new HashSet<>(MysqlSqlCompletionConfig.preferredRules());
            if (additionalPreferredRules != null) {
                core.preferredRules.addAll(additionalPreferredRules);
            }
            CodeCompletionCore.CandidatesCollection candidates =
                    core.collectCandidates(tokenIndex, currentStatementContext(parser, tokenStream));
            if (candidates == null) {
                return SqlCompletionCandidates.unavailable();
            }
            Map<Integer, SqlCompletionCandidates.RuleCandidate> rules = new HashMap<>();
            candidates.rules.forEach((rule, candidateRule) -> rules.put(rule,
                    new SqlCompletionCandidates.RuleCandidate(candidateRule.startTokenIndex,
                            candidateRule.stopTokenIndex, candidateRule.ruleList)));
            return new SqlCompletionCandidates(true, tokenIndex, candidates.tokens, rules);
        } catch (RuntimeException exception) {
            return SqlCompletionCandidates.unavailable();
        }
    }

    private static int tokenIndexAt(CommonTokenStream tokenStream, MysqlSqlCompletionCandidateContext context) {
        tokenStream.fill();
        int cutOffset = context.dummySql().insertedLength() > 0
                ? context.dummySql().insertedOffset()
                : context.cursorContext().replaceStart();
        cutOffset = Math.max(0, cutOffset);
        if (context.cursorContext().dotScoped()) {
            int dotScopedTokenIndex = MysqlSqlCompletionTokenUtil.dotScopedOwnerTokenIndex(tokenStream, cutOffset);
            if (dotScopedTokenIndex >= 0) {
                return dotScopedTokenIndex;
            }
        }
        int tokenIndex = MysqlSqlCompletionTokenUtil.tokenIndexAtOrAfterOffset(tokenStream, cutOffset);
        if (tokenIndex >= 0) {
            return tokenIndex;
        }
        return tokenIndexAtOrAfterCursor(tokenStream, context.dummySql().cursor());
    }

    private static int tokenIndexAtOrAfterCursor(CommonTokenStream tokenStream, int cursor) {
        int previousTokenIndex = -1;
        for (Token token : tokenStream.getTokens()) {
            if (token.getType() == Token.EOF) {
                return previousTokenIndex >= 0 ? previousTokenIndex : token.getTokenIndex();
            }
            if (!MysqlSqlCompletionTokenUtil.isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                return token.getTokenIndex();
            }
            previousTokenIndex = token.getTokenIndex();
        }
        return Math.max(0, previousTokenIndex);
    }

    private static ParserRuleContext currentStatementContext(MySqlParser parser, CommonTokenStream tokenStream) {
        try {
            tokenStream.seek(0);
            ParserRuleContext context = parser.sqlStatement();
            tokenStream.seek(0);
            return context;
        } catch (RuntimeException exception) {
            tokenStream.seek(0);
            return null;
        }
    }
}
