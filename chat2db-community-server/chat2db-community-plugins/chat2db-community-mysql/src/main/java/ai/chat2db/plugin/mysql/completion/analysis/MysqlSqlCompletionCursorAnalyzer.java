package ai.chat2db.plugin.mysql.completion.analysis;

import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import ai.chat2db.spi.ISqlCompletionCursorAnalyzer;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.Token;


public final class MysqlSqlCompletionCursorAnalyzer implements ISqlCompletionCursorAnalyzer {

    private static final Set<Integer> BOUNDARY_TIP_TOKENS = Set.of(
            MySqlLexer.DOT,
            MySqlLexer.COMMA,
            MySqlLexer.LR_BRACKET,
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
            MySqlLexer.BIT_XOR_OP);

    @Override
    public SqlCompletionCursorContext analyze(SqlCompletionPipelineState state) {
        return analyze(state == null ? null : state.window());
    }

    public SqlCompletionCursorContext analyze(SqlCompletionStatementWindow window) {
        if (window == null || window.empty()) {
            return SqlCompletionCursorContext.empty(0);
        }
        String sourceSql = window.sourceSql();
        String parseSql = window.parseSql();
        int cursor = Math.max(0, Math.min(window.cursor(), sourceSql.length()));
        int replaceStart = MysqlSqlCompletionTokenUtil.identifierPartStart(sourceSql, cursor);
        int replaceEnd = MysqlSqlCompletionTokenUtil.identifierPartEnd(sourceSql, cursor);
        String rawPrefix = sourceSql.substring(replaceStart, cursor);
        String prefix = MysqlSqlCompletionTokenUtil.stripQuote(rawPrefix);
        if (MysqlSqlCompletionTokenUtil.cursorInsideRawLiteralOrComment(sourceSql, cursor)
                || MysqlSqlCompletionTokenUtil.rangeTouchesRawLiteralOrComment(sourceSql, replaceStart, cursor)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        List<Token> tokens = MysqlSqlCompletionTokenUtil.tokens(parseSql);
        if (MysqlSqlCompletionTokenUtil.cursorInsideLiteralOrComment(tokens, cursor)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        if (MysqlSqlCompletionTokenUtil.cursorTouchesLexerError(tokens, cursor)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        if (MysqlSqlCompletionTokenUtil.cursorCoveredByNonCompletionToken(tokens, replaceStart, cursor)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        if (MysqlSqlCompletionTokenUtil.isLocalVariableTokenCoveringRange(tokens, replaceStart, cursor)
                && !isUserVariablePrefix(sourceSql, replaceStart)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        if (MysqlSqlCompletionTokenUtil.hasDeclaredLocalVariablePrefix(tokens, replaceStart, prefix)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        if (!isTipCursor(parseSql, tokens, replaceStart, cursor, prefix)) {
            return SqlCompletionCursorContext.empty(cursor);
        }

        SqlCompletionCursorContext dotIdMatch = resolveDotIdScopedColumn(tokens, replaceStart, replaceEnd, cursor,
                prefix);
        if (dotIdMatch.admitted()) {
            return dotIdMatch;
        }
        SqlCompletionCursorContext dotMatch = resolveDotScopedColumn(tokens, replaceStart, replaceEnd, cursor, prefix);
        if (dotMatch.admitted()) {
            return dotMatch;
        }
        return SqlCompletionCursorContext.admitted(SqlCompletionMetadataScope.empty(), prefix, replaceStart, replaceEnd,
                false);
    }

    private static SqlCompletionCursorContext resolveDotIdScopedColumn(List<Token> tokens,
                                                                       int replaceStart,
                                                                       int replaceEnd,
                                                                       int cursor,
                                                                       String prefix) {
        Token current = MysqlSqlCompletionTokenUtil.tokenCoveringRange(tokens, replaceStart, cursor);
        if (current == null || !MysqlSqlCompletionTokenUtil.isDefaultToken(current)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        if (current.getType() != MySqlLexer.DOT_ID) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        Token tableToken = MysqlSqlCompletionTokenUtil.previousDefaultToken(tokens, current.getStartIndex());
        if (tableToken == null || !MysqlSqlCompletionTokenUtil.isIdentifierToken(tableToken)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        SqlCompletionMetadataScope scope = qualifiedOwnerScope(tokens, tableToken);
        int safeReplaceStart = Math.max(current.getStartIndex() + 1, replaceStart);
        return SqlCompletionCursorContext.admitted(scope, prefix, safeReplaceStart, Math.max(replaceEnd, cursor),
                true);
    }

    private static SqlCompletionCursorContext resolveDotScopedColumn(List<Token> tokens,
                                                                     int replaceStart,
                                                                     int replaceEnd,
                                                                     int cursor,
                                                                     String prefix) {
        Token previous = MysqlSqlCompletionTokenUtil.previousDefaultToken(tokens, replaceStart);
        if (previous == null || previous.getType() != MySqlLexer.DOT) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        Token tableToken = MysqlSqlCompletionTokenUtil.previousDefaultToken(tokens, previous.getStartIndex());
        if (tableToken == null || !MysqlSqlCompletionTokenUtil.isIdentifierToken(tableToken)) {
            return SqlCompletionCursorContext.empty(cursor);
        }
        return SqlCompletionCursorContext.admitted(qualifiedOwnerScope(tokens, tableToken), prefix, replaceStart,
                replaceEnd, true);
    }

    private static SqlCompletionMetadataScope qualifiedOwnerScope(List<Token> tokens, Token tableToken) {
        String schemaName = qualifiedSchemaName(tokens, tableToken);
        String tableName = identifierName(tableToken);
        return new SqlCompletionMetadataScope(null, schemaName, tableName, null);
    }

    private static String qualifiedSchemaName(List<Token> tokens, Token tableToken) {
        Token previous = MysqlSqlCompletionTokenUtil.previousDefaultToken(tokens, tableToken.getStartIndex());
        if (previous == null) {
            return null;
        }
        if (tableToken.getType() == MySqlLexer.DOT_ID
                && MysqlSqlCompletionTokenUtil.isIdentifierToken(previous)) {
            return identifierName(previous);
        }
        if (previous.getType() != MySqlLexer.DOT) {
            return null;
        }
        Token schemaToken = MysqlSqlCompletionTokenUtil.previousDefaultToken(tokens, previous.getStartIndex());
        if (schemaToken == null || !MysqlSqlCompletionTokenUtil.isIdentifierToken(schemaToken)) {
            return null;
        }
        return identifierName(schemaToken);
    }

    private static String identifierName(Token token) {
        return MysqlSqlCompletionTokenUtil.stripQuote(MysqlSqlCompletionTokenUtil.stripLeadingDot(token.getText()));
    }

    private static boolean isTipCursor(String sql, List<Token> tokens, int replaceStart, int cursor, String prefix) {
        if (cursor == 0 || !prefix.isEmpty()) {
            return true;
        }
        if (MysqlSqlCompletionTokenUtil.hasDotScope(tokens, replaceStart)) {
            return true;
        }
        Token current = MysqlSqlCompletionTokenUtil.tokenCoveringOrAfterCursor(tokens, cursor);
        if (MysqlSqlCompletionTokenUtil.isLiteralToken(current)
                || MysqlSqlCompletionTokenUtil.isCommentToken(current)
                || (current != null && current.getChannel() == MySqlLexer.ERRORCHANNEL)) {
            return false;
        }
        if (isCursorAfterWhitespace(sql, cursor)) {
            return MysqlSqlCompletionTokenUtil.hasSignificantTokenBefore(tokens, cursor);
        }
        if (current == null || current.getType() == Token.EOF) {
            return true;
        }
        if (current.getChannel() == Token.HIDDEN_CHANNEL) {
            return MysqlSqlCompletionTokenUtil.isSpaceToken(current);
        }
        if (!MysqlSqlCompletionTokenUtil.isDefaultToken(current)) {
            return false;
        }
        if (cursor == current.getStartIndex()) {
            return true;
        }
        if (cursor == current.getStopIndex() + 1) {
            return BOUNDARY_TIP_TOKENS.contains(current.getType())
                    || MysqlSqlCompletionTokenUtil.isIdentifierToken(current);
        }
        return MysqlSqlCompletionTokenUtil.isIdentifierToken(current);
    }

    private static boolean isCursorAfterWhitespace(String sql, int cursor) {
        return cursor > 0 && cursor <= sql.length() && Character.isWhitespace(sql.charAt(cursor - 1));
    }

    private static boolean isUserVariablePrefix(String sql, int replaceStart) {
        return replaceStart > 0 && replaceStart <= sql.length() && sql.charAt(replaceStart - 1) == '@';
    }

}
