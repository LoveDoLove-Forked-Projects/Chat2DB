package ai.chat2db.plugin.mysql.completion.resolver;

import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionLocalColumnScope;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.antlr.v4.runtime.Token;


public final class MysqlSqlCompletionLocalColumnScopeResolver {

    private static final Set<Integer> NON_COLUMN_DEFINITION_START_TOKENS = Set.of(
            MySqlLexer.CONSTRAINT,
            MySqlLexer.PRIMARY,
            MySqlLexer.UNIQUE,
            MySqlLexer.FOREIGN,
            MySqlLexer.CHECK,
            MySqlLexer.INDEX,
            MySqlLexer.KEY,
            MySqlLexer.FULLTEXT,
            MySqlLexer.SPATIAL);

    private MysqlSqlCompletionLocalColumnScopeResolver() {
    }

    public static MysqlSqlCompletionLocalColumnScope resolve(String sql, int cursor) {
        List<Token> tokens = MysqlSqlCompletionTokenUtil.defaultTokens(sql);
        int createIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBefore(tokens, cursor, MySqlLexer.CREATE);
        if (createIndex < 0) {
            return MysqlSqlCompletionLocalColumnScope.notApplicable();
        }
        int tableIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, createIndex + 1, cursor,
                MySqlLexer.TABLE);
        if (tableIndex < 0) {
            return MysqlSqlCompletionLocalColumnScope.notApplicable();
        }
        int tableNameIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, tableIndex + 1);
        if (tableNameIndex < 0 || !MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(tokens.get(tableNameIndex))) {
            return MysqlSqlCompletionLocalColumnScope.notApplicable();
        }
        int tableNameEndIndex = MysqlSqlCompletionTokenUtil.qualifiedIdentifierEndIndex(tokens, tableNameIndex);
        int definitionOpenIndex = definitionOpenIndex(tokens, tableNameEndIndex + 1, cursor);
        if (definitionOpenIndex < 0 || !cursorInsideDefinitions(tokens, definitionOpenIndex, cursor)) {
            return MysqlSqlCompletionLocalColumnScope.notApplicable();
        }
        LinkedHashSet<String> columns = declaredColumnsBeforeCurrentDefinition(tokens, definitionOpenIndex, cursor);
        String table = MysqlSqlCompletionTokenUtil.identifierText(tokens.get(tableNameEndIndex));
        return new MysqlSqlCompletionLocalColumnScope(true, table, List.copyOf(columns));
    }

    private static int definitionOpenIndex(List<Token> tokens, int start, int cursor) {
        for (int index = Math.max(0, start); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                return index;
            }
        }
        return -1;
    }

    private static boolean cursorInsideDefinitions(List<Token> tokens, int openIndex, int cursor) {
        int depth = 0;
        for (int index = openIndex; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                return depth > 0;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth--;
                if (depth <= 0) {
                    return false;
                }
            }
        }
        return depth > 0;
    }

    private static LinkedHashSet<String> declaredColumnsBeforeCurrentDefinition(List<Token> tokens,
                                                                               int openIndex,
                                                                               int cursor) {
        LinkedHashSet<String> columns = new LinkedHashSet<>();
        int definitionStart = openIndex + 1;
        int depth = 1;
        for (int index = openIndex + 1; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth--;
                continue;
            }
            if (depth == 1 && token.getType() == MySqlLexer.COMMA) {
                collectDefinitionColumn(tokens, definitionStart, index).ifPresent(columns::add);
                definitionStart = index + 1;
            }
        }
        return columns;
    }

    private static Optional<String> collectDefinitionColumn(List<Token> tokens, int start, int endExclusive) {
        for (int index = Math.max(0, start); index < Math.min(endExclusive, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (!MysqlSqlCompletionTokenUtil.isDefaultToken(token)) {
                continue;
            }
            if (!MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(token)
                    || NON_COLUMN_DEFINITION_START_TOKENS.contains(token.getType())
                    || MysqlSqlCompletionTokenUtil.isCompletionDummy(token)) {
                return Optional.empty();
            }
            return Optional.of(MysqlSqlCompletionTokenUtil.stripQuote(token.getText()));
        }
        return Optional.empty();
    }
}
