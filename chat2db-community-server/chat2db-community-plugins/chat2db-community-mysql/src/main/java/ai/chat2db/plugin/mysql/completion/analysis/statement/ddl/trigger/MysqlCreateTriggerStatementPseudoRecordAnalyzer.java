package ai.chat2db.plugin.mysql.completion.analysis.statement.ddl.trigger;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.Token;


public final class MysqlCreateTriggerStatementPseudoRecordAnalyzer {

    private MysqlCreateTriggerStatementPseudoRecordAnalyzer() {
    }

    public static Optional<MysqlSqlCompletionRelationScope> resolvePseudoRecordTargetRelationScope(
            MysqlSqlCompletionCandidateContext context) {
        if (!supports(context)) {
            return Optional.empty();
        }
        List<Token> tokens = MysqlSqlCompletionTokenUtil.defaultTokens(context.window().parseSql());
        int cursor = Math.max(0, Math.min(context.cursorContext().replaceStart(), context.window().parseSql().length()));
        return triggerTargetRelation(tokens, cursor)
                .map(relation -> new MysqlSqlCompletionRelationScope(List.of(relation)));
    }

    private static boolean supports(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null
                || !context.cursorContext().dotScoped()
                || context.cursorContext().scope() == null
                || !MysqlSqlCompletionTokenUtil.isPseudoRecordOwner(context.cursorContext().scope().table())
                || context.c3Result() == null
                || !context.c3Result().available()) {
            return false;
        }
        List<Token> tokens = MysqlSqlCompletionTokenUtil.defaultTokens(context.window().parseSql());
        int cursor = Math.max(0, Math.min(context.cursorContext().replaceStart(), context.window().parseSql().length()));
        return insideTriggerRoutineBody(tokens, cursor);
    }

    public static boolean insideTriggerRoutineBody(List<Token> tokens, int cursor) {
        int triggerIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBefore(tokens, cursor, MySqlLexer.TRIGGER);
        if (triggerIndex < 0) {
            return false;
        }
        int onIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, triggerIndex + 1, cursor,
                MySqlLexer.ON);
        if (onIndex < 0) {
            return false;
        }
        int eachIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, onIndex + 1, cursor,
                MySqlLexer.EACH);
        if (eachIndex < 0) {
            return false;
        }
        int rowIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, eachIndex + 1, cursor,
                MySqlLexer.ROW);
        return rowIndex >= 0;
    }

    private static Optional<MysqlSqlCompletionRelationScope.Relation> triggerTargetRelation(List<Token> tokens,
                                                                                           int cursor) {
        int triggerIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBefore(tokens, cursor, MySqlLexer.TRIGGER);
        if (triggerIndex < 0) {
            return Optional.empty();
        }
        int onIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, triggerIndex + 1, cursor,
                MySqlLexer.ON);
        if (onIndex < 0) {
            return Optional.empty();
        }
        int tableStart = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, onIndex + 1);
        if (tableStart < 0 || !MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(tokens.get(tableStart))) {
            return Optional.empty();
        }
        int tableEnd = qualifiedRelationEndIndex(tokens, tableStart);
        QualifiedRelationName relationName = qualifiedRelationName(tokens, tableStart, tableEnd);
        if (relationName.table().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new MysqlSqlCompletionRelationScope.Relation(
                relationName.catalog(), relationName.schema(), relationName.table(), null));
    }

    private static int qualifiedRelationEndIndex(List<Token> tokens, int start) {
        int end = start;
        int index = start + 1;
        while (index < tokens.size()) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.DOT_ID) {
                end = index;
                index++;
                continue;
            }
            if (token.getType() == MySqlLexer.DOT
                    && index + 1 < tokens.size()
                    && MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(tokens.get(index + 1))) {
                end = index + 1;
                index += 2;
                continue;
            }
            break;
        }
        return end;
    }

    private static QualifiedRelationName qualifiedRelationName(List<Token> tokens, int start, int end) {
        List<String> parts = new ArrayList<>();
        for (int index = start; index <= end && index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.DOT) {
                continue;
            }
            if (token.getType() == MySqlLexer.DOT_ID) {
                parts.add(MysqlSqlCompletionTokenUtil.stripQuote(
                        MysqlSqlCompletionTokenUtil.stripLeadingDot(token.getText())));
                continue;
            }
            if (MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(token)) {
                parts.add(MysqlSqlCompletionTokenUtil.stripQuote(token.getText()));
            }
        }
        if (parts.size() >= 3) {
            return new QualifiedRelationName(parts.get(parts.size() - 3), parts.get(parts.size() - 2),
                    parts.get(parts.size() - 1));
        }
        if (parts.size() == 2) {
            return new QualifiedRelationName(null, parts.get(0), parts.get(1));
        }
        return new QualifiedRelationName(null, null, parts.isEmpty() ? "" : parts.get(0));
    }

    private record QualifiedRelationName(String catalog,
                                         String schema,
                                         String table) {
    }
}
