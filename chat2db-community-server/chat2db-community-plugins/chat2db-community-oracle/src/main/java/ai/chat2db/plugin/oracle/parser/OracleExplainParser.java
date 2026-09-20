package ai.chat2db.plugin.oracle.parser;

import ai.chat2db.plugin.oracle.parser.base.PlSqlLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Recognizes the Oracle {@code EXPLAIN PLAN} command and extracts the statement
 * it explains.
 *
 * <p>{@code EXPLAIN PLAN ... FOR <statement>} never returns a result set: it
 * only writes plan rows into the plan table. Chat2DB reads those rows back and
 * needs the explained statement on its own, so the command is inspected through
 * the PL/SQL lexer instead of being rewritten with text patterns.
 */
public final class OracleExplainParser {

    private static final String EXPLAIN_KEYWORD = "EXPLAIN";
    private static final String PLAN_KEYWORD = "PLAN";
    private static final String FOR_KEYWORD = "FOR";
    private static final String INTO_KEYWORD = "INTO";
    private static final String STATEMENT_DELIMITER = ";";

    private OracleExplainParser() {
    }

    /**
     * Returns the statement that {@code sql} explains, or {@code null} when
     * {@code sql} is not an explain command Chat2DB reads a plan for.
     *
     * <p>{@code EXPLAIN PLAN ... INTO <table>} writes to a caller supplied plan
     * table and is left to the database, and any other statement is not an
     * explain command.
     */
    public static String extractExplainedSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }
        List<Token> tokens = tokenize(sql);
        int index = nextValuableToken(tokens, 0);
        if (!isKeyword(tokens, index, EXPLAIN_KEYWORD)) {
            return null;
        }
        index = nextValuableToken(tokens, index + 1);
        if (!isKeyword(tokens, index, PLAN_KEYWORD)) {
            return null;
        }
        for (index = nextValuableToken(tokens, index + 1); index >= 0;
                index = nextValuableToken(tokens, index + 1)) {
            if (isKeyword(tokens, index, INTO_KEYWORD)) {
                return null;
            }
            if (isKeyword(tokens, index, FOR_KEYWORD)) {
                return explainedSql(tokens, sql, index);
            }
        }
        return null;
    }

    private static String explainedSql(List<Token> tokens, String sql, int forIndex) {
        int firstTokenIndex = nextValuableToken(tokens, forIndex + 1);
        int lastTokenIndex = lastValuableToken(tokens);
        if (firstTokenIndex < 0 || lastTokenIndex < firstTokenIndex) {
            return null;
        }
        String explainedSql = sql.substring(tokens.get(firstTokenIndex).getStartIndex(),
                tokens.get(lastTokenIndex).getStopIndex() + 1).trim();
        if (explainedSql.endsWith(STATEMENT_DELIMITER)) {
            explainedSql = explainedSql.substring(0, explainedSql.length() - 1).trim();
        }
        return StringUtils.isBlank(explainedSql) ? null : explainedSql;
    }

    private static List<Token> tokenize(String sql) {
        PlSqlLexer lexer = new PlSqlLexer(CharStreams.fromString(sql));
        lexer.removeErrorListeners();
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        return tokenStream.getTokens();
    }

    private static int nextValuableToken(List<Token> tokens, int fromIndex) {
        for (int i = Math.max(fromIndex, 0); i < tokens.size(); i++) {
            if (isValuable(tokens.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int lastValuableToken(List<Token> tokens) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (isValuable(tokens.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isValuable(Token token) {
        return token.getType() != Token.EOF && token.getChannel() == Token.DEFAULT_CHANNEL;
    }

    private static boolean isKeyword(List<Token> tokens, int index, String keyword) {
        return index >= 0 && StringUtils.equalsIgnoreCase(tokens.get(index).getText(), keyword);
    }
}
