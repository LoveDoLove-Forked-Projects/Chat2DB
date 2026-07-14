package ai.chat2db.plugin.mysql.completion.util;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionInputCleanResponse;
import ai.chat2db.spi.util.SqlCompletionTemplatePlaceholderCleaner;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.antlr.v4.runtime.Token;


public final class MysqlSqlCompletionInputCleaner {

    private static final Set<Integer> QUESTION_EXPRESSION_PREVIOUS_TOKENS = Set.of(
            MySqlLexer.COMMA,
            MySqlLexer.EQUAL_SYMBOL,
            MySqlLexer.GREATER_SYMBOL,
            MySqlLexer.LESS_SYMBOL,
            MySqlLexer.EXCLAMATION_SYMBOL,
            MySqlLexer.PLUS,
            MySqlLexer.MINUS,
            MySqlLexer.STAR,
            MySqlLexer.DIVIDE,
            MySqlLexer.MODULE,
            MySqlLexer.BIT_NOT_OP,
            MySqlLexer.BIT_OR_OP,
            MySqlLexer.BIT_AND_OP,
            MySqlLexer.BIT_XOR_OP,
            MySqlLexer.WHERE,
            MySqlLexer.AND,
            MySqlLexer.OR,
            MySqlLexer.ON,
            MySqlLexer.HAVING,
            MySqlLexer.SET,
            MySqlLexer.CHECK,
            MySqlLexer.IF,
            MySqlLexer.THEN,
            MySqlLexer.ELSE,
            MySqlLexer.WHEN,
            MySqlLexer.BETWEEN,
            MySqlLexer.IN,
            MySqlLexer.VALUE,
            MySqlLexer.VALUES,
            MySqlLexer.LIMIT,
            MySqlLexer.SELECT);

    private static final Set<Integer> NON_EXPRESSION_IDENTIFIER_OPENING_PREVIOUS_TOKENS = Set.of(
            MySqlLexer.TABLE,
            MySqlLexer.INDEX,
            MySqlLexer.KEY,
            MySqlLexer.REFERENCES);

    private MysqlSqlCompletionInputCleaner() {
    }

    public static SqlCompletionInputCleanResponse clean(String sql, int cursor) {
        String sourceSql = Objects.toString(sql, "");
        String parseSql = sourceSql;
        parseSql = maskMysqlVerticalResultSuffix(parseSql);
        parseSql = SqlCompletionTemplatePlaceholderCleaner.clean(parseSql);
        parseSql = maskByMysqlTokens(parseSql, cursor);
        return new SqlCompletionInputCleanResponse(sourceSql, parseSql, cursor);
    }

    private static String maskMysqlVerticalResultSuffix(String sql) {
        int suffixIndex = sql.lastIndexOf("\\G");
        if (suffixIndex < 0 || suffixIndex + 2 != sql.length()) {
            return sql;
        }
        char[] chars = sql.toCharArray();
        chars[suffixIndex] = ' ';
        chars[suffixIndex + 1] = ' ';
        return new String(chars);
    }

    private static String maskByMysqlTokens(String sql, int cursor) {
        if (sql.isEmpty()) {
            return sql;
        }
        int safeCursor = Math.max(0, Math.min(cursor, sql.length()));
        char[] chars = sql.toCharArray();
        List<Token> tokens = MysqlSqlCompletionTokenUtil.tokens(sql);
        maskLexerErrors(chars, tokens, safeCursor);
        return new String(chars);
    }

    private static void maskLexerErrors(char[] chars, List<Token> tokens, int cursor) {
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getType() == Token.EOF || token.getChannel() != MySqlLexer.ERRORCHANNEL) {
                continue;
            }
            int start = token.getStartIndex();
            int stop = token.getStopIndex();
            if (start < 0 || stop < start) {
                continue;
            }
            if (cursor >= start && cursor <= stop + 1) {
                continue;
            }
            if (isQuestionExpressionPlaceholder(tokens, index, token)) {
                chars[start] = '0';
                continue;
            }
            mask(chars, start, stop + 1);
        }
    }

    private static boolean isQuestionExpressionPlaceholder(List<Token> tokens, int index, Token token) {
        if (!"?".equals(token.getText())) {
            return false;
        }
        int previousIndex = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, index - 1);
        if (previousIndex < 0) {
            return false;
        }
        Token previous = tokens.get(previousIndex);
        if (previous.getType() == MySqlLexer.LR_BRACKET) {
            return isExpressionOpeningBracket(tokens, previousIndex);
        }
        return QUESTION_EXPRESSION_PREVIOUS_TOKENS.contains(previous.getType());
    }

    private static boolean isExpressionOpeningBracket(List<Token> tokens, int bracketIndex) {
        int beforeBracketIndex = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, bracketIndex - 1);
        if (beforeBracketIndex < 0) {
            return true;
        }
        Token beforeBracket = tokens.get(beforeBracketIndex);
        if (QUESTION_EXPRESSION_PREVIOUS_TOKENS.contains(beforeBracket.getType())) {
            return true;
        }
        if (!MysqlSqlCompletionTokenUtil.isIdentifierToken(beforeBracket)
                || isInsertColumnListOpening(tokens, beforeBracketIndex)
                || isCreateIndexColumnListOpening(tokens, beforeBracketIndex)) {
            return false;
        }
        int beforeIdentifierIndex = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, beforeBracketIndex - 1);
        return beforeIdentifierIndex < 0
                || !NON_EXPRESSION_IDENTIFIER_OPENING_PREVIOUS_TOKENS.contains(tokens.get(beforeIdentifierIndex).getType());
    }

    private static boolean isInsertColumnListOpening(List<Token> tokens, int tableNameEndIndex) {
        int intoIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBefore(tokens,
                tokens.get(tableNameEndIndex).getStartIndex(), MySqlLexer.INTO);
        if (intoIndex < 0) {
            return false;
        }
        int tableNameIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, intoIndex + 1);
        if (tableNameIndex < 0) {
            return false;
        }
        return MysqlSqlCompletionTokenUtil.qualifiedIdentifierEndIndex(tokens, tableNameIndex) == tableNameEndIndex;
    }

    private static boolean isCreateIndexColumnListOpening(List<Token> tokens, int tableNameEndIndex) {
        int onIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBefore(tokens,
                tokens.get(tableNameEndIndex).getStartIndex(), MySqlLexer.ON);
        if (onIndex < 0 || MysqlSqlCompletionTokenUtil.missingOrderedTokenTypesBefore(tokens, onIndex,
                MySqlLexer.CREATE, MySqlLexer.INDEX)) {
            return false;
        }
        int tableNameIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, onIndex + 1);
        if (tableNameIndex < 0) {
            return false;
        }
        return MysqlSqlCompletionTokenUtil.qualifiedIdentifierEndIndex(tokens, tableNameIndex) == tableNameEndIndex;
    }

    private static void mask(char[] chars, int start, int end) {
        for (int i = Math.max(0, start); i < end && i < chars.length; i++) {
            chars[i] = ' ';
        }
    }
}
