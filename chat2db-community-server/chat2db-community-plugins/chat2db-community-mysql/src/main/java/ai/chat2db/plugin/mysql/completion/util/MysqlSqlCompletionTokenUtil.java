package ai.chat2db.plugin.mysql.completion.util;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;


import static ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionTokenUtilConstants.*;
public final class MysqlSqlCompletionTokenUtil {


    private static final Map<String, Integer> KEYWORD_TOKEN_TYPES = keywordTokenTypes();

    private static final Set<Integer> COMMENT_TOKENS = Set.of(
            MySqlLexer.SPEC_MYSQL_COMMENT,
            MySqlLexer.COMMENT_INPUT,
            MySqlLexer.LINE_COMMENT);

    private static final Set<Integer> LITERAL_TOKENS = Set.of(
            MySqlLexer.START_NATIONAL_STRING_LITERAL,
            MySqlLexer.DECIMAL_LITERAL,
            MySqlLexer.HEXADECIMAL_LITERAL,
            MySqlLexer.REAL_LITERAL,
            MySqlLexer.NULL_SPEC_LITERAL,
            MySqlLexer.BIT_STRING,
            MySqlLexer.ZERO_DECIMAL,
            MySqlLexer.ONE_DECIMAL,
            MySqlLexer.TWO_DECIMAL,
            MySqlLexer.NULL_LITERAL,
            MySqlLexer.TRUE,
            MySqlLexer.FALSE);

    private static final Set<Integer> FIXED_ARGUMENT_AGGREGATE_FUNCTIONS = Set.of(
            MySqlLexer.AVG,
            MySqlLexer.MAX,
            MySqlLexer.MIN,
            MySqlLexer.SUM);

    private static final Set<Integer> DATA_TYPE_NAME_TOKENS = Set.of(
            MySqlLexer.SMALLINT,
            MySqlLexer.MEDIUMINT,
            MySqlLexer.MIDDLEINT,
            MySqlLexer.INT,
            MySqlLexer.INT1,
            MySqlLexer.INT2,
            MySqlLexer.INT3,
            MySqlLexer.INT4,
            MySqlLexer.INT8,
            MySqlLexer.INTEGER,
            MySqlLexer.BIGINT,
            MySqlLexer.REAL,
            MySqlLexer.DOUBLE,
            MySqlLexer.PRECISION,
            MySqlLexer.FLOAT,
            MySqlLexer.FLOAT4,
            MySqlLexer.FLOAT8,
            MySqlLexer.DECIMAL,
            MySqlLexer.DEC,
            MySqlLexer.NUMERIC,
            MySqlLexer.DATE,
            MySqlLexer.TIME,
            MySqlLexer.TIMESTAMP,
            MySqlLexer.DATETIME,
            MySqlLexer.YEAR,
            MySqlLexer.CHAR,
            MySqlLexer.VARCHAR,
            MySqlLexer.NVARCHAR,
            MySqlLexer.NATIONAL,
            MySqlLexer.BINARY,
            MySqlLexer.VARBINARY,
            MySqlLexer.TINYBLOB,
            MySqlLexer.BLOB,
            MySqlLexer.MEDIUMBLOB,
            MySqlLexer.LONGBLOB,
            MySqlLexer.TINYTEXT,
            MySqlLexer.TEXT,
            MySqlLexer.MEDIUMTEXT,
            MySqlLexer.LONGTEXT,
            MySqlLexer.ENUM,
            MySqlLexer.SET,
            MySqlLexer.SERIAL,
            MySqlLexer.VECTOR,
            MySqlLexer.BIT,
            MySqlLexer.BOOL,
            MySqlLexer.BOOLEAN,
            MySqlLexer.JSON);

    private MysqlSqlCompletionTokenUtil() {
    }

    public static CommonTokenStream tokenStream(String sql) {
        MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(sql == null ? "" : sql));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        return tokenStream;
    }

    public static List<Token> tokens(String sql) {
        return tokenStream(sql).getTokens();
    }

    public static List<Token> defaultTokens(String sql) {
        return tokens(sql).stream()
                .filter(MysqlSqlCompletionTokenUtil::isDefaultToken)
                .toList();
    }

    public static List<Token> significantDefaultTokens(String sql) {
        return defaultTokens(sql).stream()
                .filter(token -> !isSpaceToken(token))
                .toList();
    }

    public static Token firstEffectiveDefaultToken(String sql) {
        return defaultTokens(sql).stream()
                .filter(token -> !isSpaceToken(token))
                .filter(token -> !isCommentToken(token))
                .findFirst()
                .orElse(null);
    }

    public static boolean startsWithToken(String sql, int tokenType) {
        return startsWithToken(defaultTokens(sql), tokenType);
    }

    public static boolean startsWithToken(List<Token> tokens, int tokenType) {
        if (tokens == null || tokens.isEmpty()) {
            return false;
        }
        return tokens.stream()
                .filter(MysqlSqlCompletionTokenUtil::isDefaultToken)
                .findFirst()
                .map(token -> token.getType() == tokenType)
                .orElse(false);
    }

    public static boolean isSetAssignmentTargetSlot(String sql, int cursor) {
        String value = sql == null ? "" : sql;
        int safeCursor = Math.max(0, Math.min(cursor, value.length()));
        List<Token> tokens = defaultTokens(value);
        boolean insideSetClause = false;
        boolean assignmentOperatorSeen = false;
        int depth = 0;
        for (Token token : tokens) {
            if (token.getStartIndex() >= safeCursor) {
                break;
            }
            if (depth == 0) {
                switch (token.getType()) {
                    case MySqlLexer.SET -> {
                        insideSetClause = true;
                        assignmentOperatorSeen = false;
                    }
                    case MySqlLexer.COMMA -> {
                        if (insideSetClause) {
                            assignmentOperatorSeen = false;
                        }
                    }
                    case MySqlLexer.EQUAL_SYMBOL -> {
                        if (insideSetClause) {
                            assignmentOperatorSeen = true;
                        }
                    }
                    case MySqlLexer.WHERE,
                            MySqlLexer.ORDER,
                            MySqlLexer.GROUP,
                            MySqlLexer.HAVING,
                            MySqlLexer.LIMIT,
                            MySqlLexer.VALUES,
                            MySqlLexer.VALUE -> insideSetClause = false;
                    default -> {
                    }
                }
            }
            depth = updateBracketDepth(depth, token);
        }
        return insideSetClause && !assignmentOperatorSeen;
    }

    public static boolean isDefaultToken(Token token) {
        return token != null
                && token.getType() != Token.EOF
                && token.getChannel() == Token.DEFAULT_CHANNEL;
    }

    public static Token previousDefaultToken(List<Token> tokens, int offset) {
        Token previous = null;
        for (Token token : tokens) {
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStopIndex() >= offset) {
                break;
            }
            previous = token;
        }
        return previous;
    }

    public static int previousDefaultIndex(List<Token> tokens, int start) {
        for (int index = Math.min(start, tokens.size() - 1); index >= 0; index--) {
            if (isDefaultToken(tokens.get(index))) {
                return index;
            }
        }
        return -1;
    }


    public static int previousDefaultIndex(List<Token> tokens, int start, int... tokenTypes) {
        if (tokenTypes == null || tokenTypes.length == 0) {
            return previousDefaultIndex(tokens, start);
        }
        for (int index = Math.min(start, tokens.size() - 1); index >= 0; index--) {
            Token token = tokens.get(index);
            if (isDefaultToken(token) && tokenMatches(token, tokenTypes)) {
                return index;
            }
        }
        return -1;
    }

    public static Token nextDefaultToken(List<Token> tokens, int startIndex) {
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (isDefaultToken(token)) {
                return token;
            }
        }
        return null;
    }

    public static int nextDefaultIndex(List<Token> tokens, int startIndex) {
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            if (isDefaultToken(tokens.get(index))) {
                return index;
            }
        }
        return -1;
    }


    public static int nextDefaultIndexIfType(List<Token> tokens, int startIndex, int tokenType) {
        int index = nextDefaultIndex(tokens, startIndex);
        return index >= 0 && tokens.get(index).getType() == tokenType ? index : -1;
    }

    public static boolean hasTokenBefore(List<Token> tokens, int cursor, int tokenType) {
        for (Token token : tokens) {
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == tokenType) {
                return true;
            }
        }
        return false;
    }

    public static int lastDefaultIndexBefore(List<Token> tokens, int cursor, int tokenType) {
        int result = -1;
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == tokenType) {
                result = index;
            }
        }
        return result;
    }

    public static int nextDefaultIndexBefore(List<Token> tokens, int startIndex, int cursor, int tokenType) {
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == tokenType) {
                return index;
            }
        }
        return -1;
    }

    public static int firstDefaultIndex(List<Token> tokens, int start, int endExclusive, int tokenType) {
        for (int index = Math.max(0, start); index < Math.min(endExclusive, tokens.size()); index++) {
            if (tokens.get(index).getType() == tokenType) {
                return index;
            }
        }
        return -1;
    }

    public static int nextDefaultIndexBeforeAtDepth(List<Token> tokens,
                                                    int start,
                                                    int cursor,
                                                    int depth,
                                                    int... tokenTypes) {
        int currentDepth = 0;
        int safeStart = Math.max(0, start);
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                return -1;
            }
            if (index >= safeStart && currentDepth == depth && tokenMatches(token, tokenTypes)) {
                return index;
            }
            currentDepth = updateBracketDepth(currentDepth, token);
        }
        return -1;
    }

    public static int firstDefaultIndexBeforeAtDepth(List<Token> tokens, int cursor, int depth, int tokenType) {
        return nextDefaultIndexBeforeAtDepth(tokens, 0, cursor, depth, tokenType);
    }

    public static int lastDefaultIndexBeforeAtDepth(List<Token> tokens,
                                                    int cursor,
                                                    int depth,
                                                    int... tokenTypes) {
        int result = -1;
        int currentDepth = 0;
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (currentDepth == depth && tokenMatches(token, tokenTypes)) {
                result = index;
            }
            currentDepth = updateBracketDepth(currentDepth, token);
        }
        return result;
    }

    public static boolean missingOrderedTokenTypesBefore(List<Token> tokens, int endExclusiveIndex, int... tokenTypes) {
        return !hasOrderedTokenTypesBetween(tokens, 0, endExclusiveIndex, tokenTypes);
    }

    public static boolean hasOrderedTokenTypesBetween(List<Token> tokens,
                                                      int startInclusiveIndex,
                                                      int endExclusiveIndex,
                                                      int... tokenTypes) {
        if (tokenTypes == null || tokenTypes.length == 0) {
            return true;
        }
        int matched = 0;
        int safeStart = Math.max(0, startInclusiveIndex);
        int safeEnd = Math.max(0, Math.min(endExclusiveIndex, tokens.size()));
        for (int index = safeStart; index < safeEnd; index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getType() == tokenTypes[matched]) {
                matched++;
                if (matched == tokenTypes.length) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Token tokenCoveringRange(List<Token> tokens, int start, int end) {
        for (Token token : tokens) {
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() <= start && token.getStopIndex() + 1 >= end) {
                return token;
            }
            if (token.getStartIndex() > start) {
                break;
            }
        }
        return null;
    }

    public static boolean isLocalVariableTokenCoveringRange(List<Token> tokens, int start, int end) {
        Token token = tokenCoveringRange(tokens, start, end);
        return token != null && token.getType() == MySqlLexer.LOCAL_ID;
    }

    public static Token tokenCoveringOrAfterCursor(List<Token> tokens, int cursor) {
        for (Token token : tokens) {
            if (token.getType() == Token.EOF) {
                return token;
            }
            if (token.getStartIndex() <= cursor && token.getStopIndex() + 1 >= cursor) {
                return token;
            }
            if (token.getStartIndex() > cursor) {
                return token;
            }
        }
        return null;
    }

    public static Token tokenCoveringCursor(List<Token> tokens, int cursor) {
        for (Token token : tokens) {
            if (token.getType() == Token.EOF) {
                break;
            }
            if (token.getStartIndex() > cursor) {
                break;
            }
            if (token.getStartIndex() <= cursor && token.getStopIndex() + 1 >= cursor) {
                return token;
            }
        }
        return null;
    }

    public static int tokenIndexAtOrAfterOffset(CommonTokenStream tokenStream, int offset) {
        int previousTokenIndex = -1;
        for (Token token : tokenStream.getTokens()) {
            if (token.getType() == Token.EOF) {
                return previousTokenIndex >= 0 ? previousTokenIndex : token.getTokenIndex();
            }
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= offset) {
                return token.getTokenIndex();
            }
            previousTokenIndex = token.getTokenIndex();
        }
        return previousTokenIndex;
    }

    public static int dotScopedOwnerTokenIndex(CommonTokenStream tokenStream, int offset) {
        for (Token token : tokenStream.getTokens()) {
            if (token.getType() == Token.EOF) {
                break;
            }
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getType() == MySqlLexer.DOT_ID
                    && token.getStartIndex() < offset
                    && token.getStopIndex() + 1 >= offset) {
                return tokenIndexBeforeOffset(tokenStream, token.getStartIndex());
            }
            if (token.getType() == MySqlLexer.DOT && token.getStartIndex() < offset) {
                return tokenIndexBeforeOffset(tokenStream, token.getStartIndex());
            }
            if (token.getStartIndex() >= offset) {
                break;
            }
        }
        return -1;
    }

    public static int tokenIndexBeforeOffset(CommonTokenStream tokenStream, int offset) {
        int previous = -1;
        for (Token token : tokenStream.getTokens()) {
            if (token.getType() == Token.EOF) {
                break;
            }
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= offset) {
                break;
            }
            previous = token.getTokenIndex();
        }
        return previous;
    }

    public static boolean tokenEndsAtOrBefore(Token token, int cursor) {
        return token != null && token.getStopIndex() + 1 <= cursor;
    }

    public static boolean containsCursor(List<Token> tokens, int openIndex, int closeIndex, int cursor) {
        if (tokens == null || openIndex < 0 || closeIndex < 0
                || openIndex >= tokens.size() || closeIndex >= tokens.size()) {
            return false;
        }
        Token open = tokens.get(openIndex);
        Token close = tokens.get(closeIndex);
        return open != null && close != null
                && open.getStartIndex() < cursor
                && cursor <= close.getStopIndex();
    }


    public static boolean cursorInsideOpenEndedBracketRange(List<Token> tokens, int openIndex, int cursor) {
        if (tokens == null || openIndex < 0 || openIndex >= tokens.size()) {
            return false;
        }
        Token open = tokens.get(openIndex);
        if (open == null || open.getType() != MySqlLexer.LR_BRACKET || open.getStartIndex() >= cursor) {
            return false;
        }
        int closeIndex = matchingRightBracket(tokens, openIndex);
        return closeIndex < 0 || cursor <= tokens.get(closeIndex).getStartIndex();
    }

    public static int qualifiedIdentifierEndIndex(List<Token> tokens, int start) {
        if (start < 0 || start >= tokens.size()) {
            return -1;
        }
        if (!isUnqualifiedIdentifierToken(tokens.get(start))) {
            return start;
        }
        if (start + 1 < tokens.size() && tokens.get(start + 1).getType() == MySqlLexer.DOT_ID) {
            return start + 1;
        }
        if (start + 2 < tokens.size()
                && tokens.get(start + 1).getType() == MySqlLexer.DOT
                && isUnqualifiedIdentifierToken(tokens.get(start + 2))) {
            return start + 2;
        }
        return start;
    }

    public static int depthAtOffset(List<Token> tokens, int cursor) {
        int depth = 0;
        for (Token token : tokens) {
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                break;
            }
            depth = updateBracketDepth(depth, token);
        }
        return depth;
    }

    public static boolean startsSubquery(List<Token> tokens, int openIndex) {
        int nextIndex = nextDefaultIndex(tokens, openIndex + 1);
        return nextIndex >= 0 && tokens.get(nextIndex).getType() == MySqlLexer.SELECT;
    }

    public static int matchingRightBracket(List<Token> tokens, int openIndex) {
        return matchingRightBracket(tokens, openIndex, tokens == null ? 0 : tokens.size());
    }

    public static int matchingRightBracket(List<Token> tokens, int openIndex, int endExclusive) {
        if (tokens == null || openIndex < 0 || openIndex >= tokens.size()) {
            return -1;
        }
        int depth = 0;
        for (int index = Math.max(0, openIndex); index < Math.min(endExclusive, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth--;
                if (depth == 0) {
                    return index;
                }
            }
        }
        return -1;
    }

    public static boolean cursorTouchesLexerError(List<Token> tokens, int cursor) {
        for (Token token : tokens) {
            if (token.getType() == Token.EOF) {
                break;
            }
            if (token.getStartIndex() > cursor) {
                break;
            }
            if (token.getChannel() == MySqlLexer.ERRORCHANNEL
                    && token.getStartIndex() <= cursor
                    && token.getStopIndex() + 1 >= cursor) {
                return true;
            }
        }
        return false;
    }

    public static boolean cursorInsideLiteralOrComment(List<Token> tokens, int cursor) {
        Token token = tokenCoveringCursor(tokens, cursor);
        return isLiteralToken(token) || isCommentToken(token);
    }

    public static boolean cursorInsideRawLiteralOrComment(String sql, int cursor) {
        String value = sql == null ? "" : sql;
        int safeCursor = Math.max(0, Math.min(cursor, value.length()));
        boolean singleQuote = false;
        boolean doubleQuote = false;
        boolean lineComment = false;
        boolean blockComment = false;
        for (int index = 0; index < safeCursor; index++) {
            char current = value.charAt(index);
            char next = index + 1 < safeCursor ? value.charAt(index + 1) : '\0';
            if (lineComment) {
                if (current == '\n' || current == '\r') {
                    lineComment = false;
                }
                continue;
            }
            if (blockComment) {
                if (current == '*' && next == '/') {
                    blockComment = false;
                    index++;
                }
                continue;
            }
            if (singleQuote) {
                if (current == '\\') {
                    index++;
                    continue;
                }
                if (current == '\'' && next == '\'') {
                    index++;
                    continue;
                }
                if (current == '\'') {
                    singleQuote = false;
                }
                continue;
            }
            if (doubleQuote) {
                if (current == '\\') {
                    index++;
                    continue;
                }
                if (current == '"' && next == '"') {
                    index++;
                    continue;
                }
                if (current == '"') {
                    doubleQuote = false;
                }
                continue;
            }
            if (current == '-' && next == '-') {
                lineComment = true;
                index++;
                continue;
            }
            if (current == '#') {
                lineComment = true;
                continue;
            }
            if (current == '/' && next == '*') {
                blockComment = true;
                index++;
                continue;
            }
            if (current == '\'') {
                singleQuote = true;
                continue;
            }
            if (current == '"') {
                doubleQuote = true;
            }
        }
        return singleQuote || doubleQuote || lineComment || blockComment;
    }

    public static boolean rangeTouchesRawLiteralOrComment(String sql, int start, int end) {
        String value = sql == null ? "" : sql;
        int safeStart = Math.max(0, Math.min(start, value.length()));
        int safeEnd = Math.max(safeStart, Math.min(end, value.length()));
        for (int offset = safeStart; offset <= safeEnd; offset++) {
            if (cursorInsideRawLiteralOrComment(value, offset)) {
                return true;
            }
        }
        return false;
    }

    public static boolean cursorCoveredByNonCompletionToken(List<Token> tokens, int replaceStart, int cursor) {
        for (Token token : tokens) {
            if (token.getType() == Token.EOF) {
                break;
            }
            if (token.getStartIndex() > cursor) {
                break;
            }
            if (token.getStartIndex() <= replaceStart
                    && token.getStopIndex() + 1 >= cursor
                    && isNonCompletionToken(token)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNonCompletionToken(Token token) {
        return token != null
                && (token.getChannel() == MySqlLexer.ERRORCHANNEL
                || isCommentToken(token)
                || isLiteralToken(token));
    }

    public static boolean hasDotScope(List<Token> tokens, int replaceStart) {
        Token current = tokenCoveringRange(tokens, replaceStart, replaceStart);
        if (current != null && current.getType() == MySqlLexer.DOT_ID) {
            return true;
        }
        Token previous = previousDefaultToken(tokens, replaceStart);
        return previous != null && previous.getType() == MySqlLexer.DOT;
    }

    public static boolean isPseudoRecordOwner(String owner) {
        String normalized = stripQuote(stripLeadingDot(owner));
        return "NEW".equalsIgnoreCase(normalized) || "OLD".equalsIgnoreCase(normalized);
    }

    public static boolean hasDeclaredLocalVariablePrefix(List<Token> tokens, int replaceStart, String prefix) {
        String normalizedPrefix = stripQuote(stripLeadingDot(prefix));
        if (normalizedPrefix.isEmpty() || tokens == null) {
            return false;
        }
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= replaceStart) {
                break;
            }
            if (token.getType() != MySqlLexer.DECLARE) {
                continue;
            }
            if (hasStatementSeparatorBefore(tokens, index + 1, replaceStart)) {
                continue;
            }
            if (declaresVariableWithPrefix(tokens, index + 1, replaceStart, normalizedPrefix)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasStatementSeparatorBefore(List<Token> tokens, int startIndex, int cursor) {
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == MySqlLexer.SEMI) {
                return true;
            }
        }
        return false;
    }

    private static boolean declaresVariableWithPrefix(List<Token> tokens,
                                                      int startIndex,
                                                      int replaceStart,
                                                      String prefix) {
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= replaceStart || token.getType() == MySqlLexer.SEMI) {
                return false;
            }
            if (!isIdentifierToken(token)) {
                return false;
            }
            if (startsWithIgnoreCase(stripQuote(stripLeadingDot(token.getText())), prefix)) {
                return true;
            }
            int nextIndex = nextDefaultIndex(tokens, index + 1);
            if (nextIndex < 0 || tokens.get(nextIndex).getStartIndex() >= replaceStart) {
                return false;
            }
            int nextType = tokens.get(nextIndex).getType();
            if (nextType == MySqlLexer.COMMA) {
                index = nextIndex;
                continue;
            }
            return false;
        }
        return false;
    }

    public static boolean isInvalidFixedAggregateExtraArgument(List<Token> tokens, int cursor) {
        int openIndex = nearestOpenParenBeforeCursor(tokens, cursor);
        if (openIndex < 0) {
            return false;
        }
        int functionIndex = previousDefaultIndex(tokens, openIndex - 1);
        if (functionIndex < 0) {
            return false;
        }
        int functionType = tokens.get(functionIndex).getType();
        if (functionType == MySqlLexer.COUNT) {
            return hasTopLevelCommaBetween(tokens, openIndex + 1, cursor)
                    && !hasTokenAtCallDepth(tokens, openIndex + 1, cursor, MySqlLexer.DISTINCT);
        }
        return FIXED_ARGUMENT_AGGREGATE_FUNCTIONS.contains(functionType)
                && hasTopLevelCommaBetween(tokens, openIndex + 1, cursor);
    }

    public static boolean isIndexColumnLengthSlot(List<Token> tokens, int cursor) {
        int openIndex = nearestOpenParenBeforeCursor(tokens, cursor);
        if (openIndex < 0) {
            return false;
        }
        int columnIndex = previousDefaultIndex(tokens, openIndex - 1);
        if (columnIndex < 0 || !isUnqualifiedIdentifierToken(tokens.get(columnIndex))) {
            return false;
        }
        int columnListOpenIndex = nearestOpenParenBeforeCursor(tokens, tokens.get(columnIndex).getStartIndex());
        if (columnListOpenIndex < 0 || tokens.get(columnListOpenIndex).getType() != MySqlLexer.LR_BRACKET) {
            return false;
        }
        int closeIndex = matchingRightBracket(tokens, openIndex);
        if (closeIndex >= 0 && !containsCursor(tokens, openIndex, closeIndex, cursor)) {
            return false;
        }
        return hasIndexDeclarationTokenBefore(tokens, columnListOpenIndex);
    }

    private static int nearestOpenParenBeforeCursor(List<Token> tokens, int cursor) {
        if (tokens == null) {
            return -1;
        }
        Deque<Integer> openIndexes = new ArrayDeque<>();
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                openIndexes.push(index);
            } else if (token.getType() == MySqlLexer.RR_BRACKET && !openIndexes.isEmpty()) {
                openIndexes.pop();
            }
        }
        return openIndexes.isEmpty() ? -1 : openIndexes.peek();
    }

    private static boolean isIndexDeclarationToken(Token token) {
        if (token == null) {
            return false;
        }
        return switch (token.getType()) {
            case MySqlLexer.PRIMARY,
                    MySqlLexer.UNIQUE,
                    MySqlLexer.FOREIGN,
                    MySqlLexer.INDEX,
                    MySqlLexer.KEY,
                    MySqlLexer.FULLTEXT,
                    MySqlLexer.SPATIAL -> true;
            default -> false;
        };
    }

    private static boolean hasIndexDeclarationTokenBefore(List<Token> tokens, int endExclusiveIndex) {
        for (int index = previousDefaultIndex(tokens, endExclusiveIndex - 1);
             index >= 0;
             index = previousDefaultIndex(tokens, index - 1)) {
            Token token = tokens.get(index);
            if (isIndexDeclarationToken(token)) {
                return true;
            }
            if (token.getType() == MySqlLexer.COMMA
                    || token.getType() == MySqlLexer.CREATE
                    || token.getType() == MySqlLexer.ALTER
                    || token.getType() == MySqlLexer.TABLE
                    || token.getType() == MySqlLexer.SEMI) {
                return false;
            }
        }
        return false;
    }

    private static boolean hasTopLevelCommaBetween(List<Token> tokens, int startIndex, int cursor) {
        return hasTokenAtCallDepth(tokens, startIndex, cursor, MySqlLexer.COMMA);
    }

    private static boolean hasTokenAtCallDepth(List<Token> tokens, int startIndex, int cursor, int tokenType) {
        int depth = 0;
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!isDefaultToken(token)) {
                continue;
            }
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (depth == 0 && token.getType() == tokenType) {
                return true;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
            } else if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
            }
        }
        return false;
    }

    public static boolean hasSignificantTokenBefore(List<Token> tokens, int cursor) {
        Token previous = previousDefaultToken(tokens, cursor);
        return previous != null
                && !isCommentToken(previous)
                && !isLiteralToken(previous);
    }

    public static boolean isAtIdentifierTokenEnd(List<Token> tokens, int start, int cursor, String prefix) {
        if (tokens == null || prefix == null || prefix.isBlank() || cursor <= start) {
            return false;
        }
        Token token = tokenCoveringRange(tokens, start, cursor);
        if (!isIdentifierToken(token)) {
            return false;
        }
        return token.getStopIndex() + 1 == cursor
                && prefix.equalsIgnoreCase(stripQuote(stripLeadingDot(token.getText())));
    }

    public static boolean isSpaceToken(Token token) {
        return token != null && token.getType() == MySqlLexer.SPACE;
    }

    public static boolean isCommentToken(Token token) {
        return token != null && COMMENT_TOKENS.contains(token.getType());
    }

    public static boolean isLiteralToken(Token token) {
        if (token == null) {
            return false;
        }
        if (LITERAL_TOKENS.contains(token.getType())) {
            return true;
        }
        if (token.getType() != MySqlLexer.STRING_LITERAL
                && token.getType() != MySqlLexer.CHARSET_REVERSE_QOUTE_STRING) {
            return false;
        }
        return token.getText() == null || !token.getText().startsWith("`");
    }

    public static boolean isDataTypeNameToken(Token token) {
        return token != null && DATA_TYPE_NAME_TOKENS.contains(token.getType());
    }

    public static int keywordTokenType(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return -1;
        }
        return KEYWORD_TOKEN_TYPES.getOrDefault(keyword.toUpperCase(Locale.ROOT), -1);
    }

    public static boolean isIdentifierToken(Token token) {
        if (!isDefaultToken(token)) {
            return false;
        }
        return switch (token.getType()) {
            case MySqlLexer.ID,
                    MySqlLexer.REVERSE_QUOTE_ID,
                    MySqlLexer.DOT_ID,
                    MySqlLexer.STRING_LITERAL,
                    MySqlLexer.CHARSET_REVERSE_QOUTE_STRING -> true;
            default -> isKeywordCanBeIdentifier(token);
        };
    }

    public static boolean isUnqualifiedIdentifierToken(Token token) {
        if (!isDefaultToken(token)) {
            return false;
        }
        return switch (token.getType()) {
            case MySqlLexer.ID,
                    MySqlLexer.REVERSE_QUOTE_ID -> true;
            case MySqlLexer.STRING_LITERAL,
                    MySqlLexer.CHARSET_REVERSE_QOUTE_STRING -> startsWithBacktick(token);
            default -> isKeywordCanBeIdentifier(token);
        };
    }

    public static boolean isKeywordCanBeIdentifier(Token token) {
        return token != null
                && token.getType() > 0
                && token.getType() < MySqlLexer.STAR;
    }

    public static boolean isCompletionDummy(Token token) {
        return token != null && COMPLETION_DUMMY_IDENTIFIER.equals(token.getText());
    }

    public static String identifierText(Token token) {
        if (!isIdentifierToken(token)) {
            return "";
        }
        return stripQuote(stripLeadingDot(token.getText()));
    }

    public static String stripQuote(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("`", "").replace("\"", "").replace("[", "").replace("]", "");
    }

    public static String stripIdentifierQuotes(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.length() < 2) {
            return trimmed;
        }
        if ((trimmed.startsWith("`") && trimmed.endsWith("`"))
                || (trimmed.startsWith("\"") && trimmed.endsWith("\""))
                || (trimmed.startsWith("'") && trimmed.endsWith("'"))
                || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    public static boolean isTriggerPseudoRecordQualifierAtCursor(String sql, int cursor) {
        String value = sql == null ? "" : sql;
        int safeCursor = Math.max(0, Math.min(cursor, value.length()));
        String before = value.substring(0, safeCursor).toLowerCase(Locale.ROOT);
        return before.endsWith("new.") || before.endsWith("old.");
    }

    private static Map<String, Integer> keywordTokenTypes() {
        Map<String, Integer> tokenTypes = new HashMap<>();
        for (int tokenType = 1; tokenType <= MySqlLexer.VOCABULARY.getMaxTokenType(); tokenType++) {
            String literal = MySqlLexer.VOCABULARY.getLiteralName(tokenType);
            if (literal == null || literal.length() < 3 || !literal.startsWith("'") || !literal.endsWith("'")) {
                continue;
            }
            String text = literal.substring(1, literal.length() - 1);
            if (!text.isBlank()) {
                tokenTypes.putIfAbsent(text.toUpperCase(Locale.ROOT), tokenType);
            }
        }
        return Map.copyOf(tokenTypes);
    }

    private static boolean startsWithBacktick(Token token) {
        return token != null && token.getText() != null && token.getText().startsWith("`");
    }

    public static String stripLeadingDot(String value) {
        if (value == null) {
            return "";
        }
        return value.startsWith(".") ? value.substring(1) : value;
    }

    public static int identifierPartStart(String sql, int cursor) {
        String value = sql == null ? "" : sql;
        int start = Math.max(0, Math.min(cursor, value.length()));
        while (start > 0 && isIdentifierPart(value.charAt(start - 1))) {
            start--;
        }
        return start;
    }

    public static int identifierPartEnd(String sql, int cursor) {
        String value = sql == null ? "" : sql;
        int end = Math.max(0, Math.min(cursor, value.length()));
        while (end < value.length() && isIdentifierPart(value.charAt(end))) {
            end++;
        }
        return end;
    }

    private static boolean tokenMatches(Token token, int... tokenTypes) {
        for (int tokenType : tokenTypes) {
            if (token.getType() == tokenType) {
                return true;
            }
        }
        return false;
    }

    public static int updateBracketDepth(int depth, Token token) {
        if (token == null) {
            return depth;
        }
        if (token.getType() == MySqlLexer.LR_BRACKET) {
            return depth + 1;
        }
        if (token.getType() == MySqlLexer.RR_BRACKET) {
            return Math.max(0, depth - 1);
        }
        return depth;
    }

    private static boolean isIdentifierPart(char value) {
        return Character.isLetterOrDigit(value) || value == '_' || value == '$';
    }

    private static boolean startsWithIgnoreCase(String value, String prefix) {
        return value != null && prefix != null
                && value.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
