package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.parser.position.TokenPosition;
import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.create.CreateTableStatement;
import ai.chat2db.community.domain.api.service.task.ITaskProgressListener;
import ai.chat2db.community.domain.api.service.db.ISqlBatchHandler;
import org.antlr.v4.runtime.Token;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses SQL text into tokens, statements, and lightweight semantic structures for a dialect.
 */
public interface ISQLParser {

    /**
     * Parses SQL text into statement metadata and known object references.
     *
     * @param sql SQL text to parse.
     * @return parser result containing statement positions and extracted references such as tables and columns.
     */
    SqlParserResponse parserStatements(String sql);

    /**
     * Parses SQL text with the lightweight parser path.
     * <p>
     * Implementations may skip expensive semantic extraction while still returning statement boundaries.
     *
     * @param sql SQL text to parse.
     * @return parser result with the lightweight statement analysis.
     */
    SqlParserResponse simpleParserStatements(String sql);

    /**
     * Tokenizes SQL text and keeps tokens from every lexer channel.
     *
     * @param sql SQL text to tokenize.
     * @return tokens including whitespace, comments, and other hidden-channel tokens.
     */
    List<Token> getAllTokens(String sql);

    /**
     * Tokenizes SQL text and returns only default-channel tokens.
     *
     * @param sql SQL text to tokenize.
     * @return tokens excluding whitespace, comments, and other hidden-channel tokens.
     */
    List<Token> getAllTokensOnDefault(String sql);

    /**
     * Builds a map from token source positions to lexer tokens.
     *
     * @param sql SQL text to tokenize.
     * @return token-position map keyed by source offsets and line/column information.
     */
    Map<TokenPosition, Token> getTokenPositionMap(String sql);

    /**
     * Parses a CREATE TABLE statement into a structured table definition.
     *
     * @param sql CREATE TABLE SQL text.
     * @return structured create-table statement result.
     */
    CreateTableStatement parserCreateTableStatement(String sql);

    /**
     * Returns keywords that can start a SQL statement for this dialect.
     *
     * @return statement-start keywords such as {@code SELECT}, {@code UPDATE}, or {@code DELETE}.
     */
    Set<String> getSqlStartKeywords();


    /**
     * Determines whether a single valid SQL statement is a query statement.
     *
     * @param sql single SQL statement to inspect.
     * @return {@code true} when the statement is a SELECT-style query; otherwise {@code false}.
     */
    boolean isSelect(String sql);

    /**
     * Splits and parses a SQL script into executable statements.
     *
     * @param sql SQL script text.
     * @return parsed script statements in execution order.
     */
    List<Statement> parserSqlScript(String sql);

    /**
     * Streams a SQL script file through the parser and sends parsed statements to a batch handler.
     * <p>
     * Implementations should report progress through the listener when file parsing advances.
     *
     * @param file SQL script file to parse.
     * @param progressListener listener that receives byte and statement progress.
     * @param sqlBatchHandler handler that receives parsed statements in batches.
     * @return number of statements parsed from the file.
     */
    int parserSqlScript(File file,
                         ITaskProgressListener progressListener,
                         ISqlBatchHandler sqlBatchHandler);

    /**
     * Validates SQL statements that reference tables and returns parser diagnostics.
     *
     * @param sql SQL text to validate.
     * @return parser result containing validation state and statement metadata.
     */
    SqlParserResponse validTableStatements(String sql);
}
