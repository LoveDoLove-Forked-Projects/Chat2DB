package ai.chat2db.spi;

import org.antlr.v4.runtime.Token;

import java.util.Set;

/**
 * Describes lexical and structural SQL rules for a database dialect.
 */
public interface ISQLDialect {

    /**
     * Returns function names recognized by the dialect.
     *
     * @return normalized function-name set.
     */
    Set<String> getFunctionNames();

    /**
     * Returns statement delimiter tokens or texts recognized by the dialect.
     *
     * @return statement delimiter set.
     */
    Set<String> getStatementDelimiters();

    /**
     * Returns keywords that begin parser blocks.
     *
     * @return block-begin keyword set.
     */
    Set<String> getBlockBeginKeywords();

    /**
     * Returns keywords that end parser blocks.
     *
     * @return block-end keyword set.
     */
    Set<String> getBlockEndKeywords();

    /**
     * Returns general keywords recognized by the dialect.
     *
     * @return keyword set.
     */
    Set<String> getKeywords();

    /**
     * Returns symbols that toggle parser block state.
     *
     * @return block-toggle symbol set.
     */
    Set<String> getBlockToggleSymbols();

    /**
     * Returns block headers that can stand alone.
     *
     * @return independent block-header set.
     */
    Set<String> getIndependentBlockHeaders();

    /**
     * Returns block headers that depend on surrounding statements.
     *
     * @return dependent block-header set.
     */
    Set<String> getDependentBlockHeaders();

    /**
     * Returns prefixes that identify dialect block headers.
     *
     * @return block-header prefix set.
     */
    Set<String> getBlockHeaderPrefixes();

    /**
     * Returns delimiter texts accepted by dialect SET commands.
     *
     * @return SET delimiter set.
     */
    Set<String> getSetDelimiters();

    /**
     * Returns keywords that can start a SQL statement.
     *
     * @return SQL-start keyword set.
     */
    Set<String> getSqlStartKeywords();

    /**
     * Returns token types that represent comments.
     *
     * @return comment token-type set.
     */
    Set<Integer> getCommentTokens();

    /**
     * Returns the parser rule manager for this dialect.
     *
     * @return dialect rule manager.
     */
    IRuleManager getRuleManager();

    /**
     * Returns prefixes that indicate inner blocks.
     *
     * @return inner-block prefix set.
     */
    Set<String> getInnerBlockPrefix();

    /**
     * Returns block headers that belong to package bodies.
     *
     * @return package-body inner block-header set.
     */
    Set<String> getPackageBodyInnerBlockHeaders();

    /**
     * Checks whether text is a recognized function name.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is a function name; otherwise {@code false}.
     */
    boolean isFunctionName(String text);

    /**
     * Checks whether text is a SET delimiter.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is a SET delimiter; otherwise {@code false}.
     */
    boolean isSetDelimiter(String text);

    /**
     * Checks whether text toggles parser block state.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is a block-toggle symbol; otherwise {@code false}.
     */
    boolean isBlockToggleSymbol(String text);

    /**
     * Checks whether a token can start a SQL statement.
     *
     * @param token token to inspect.
     * @return {@code true} when the token starts a SQL statement; otherwise {@code false}.
     */
    boolean isSqlStartKeyword(Token token);

    /**
     * Checks whether text begins a parser block.
     *
     * @param text text to inspect.
     * @return {@code true} when the text begins a block; otherwise {@code false}.
     */
    boolean isBlockBegin(String text);

    /**
     * Checks whether text ends a parser block.
     *
     * @param text text to inspect.
     * @return {@code true} when the text ends a block; otherwise {@code false}.
     */
    boolean isBlockEnd(String text);

    /**
     * Checks whether text is a statement delimiter.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is a statement delimiter; otherwise {@code false}.
     */
    boolean isStatementDelimiter(String text);

    /**
     * Checks whether text is a dialect keyword.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is a keyword; otherwise {@code false}.
     */
    boolean isKeyword(String text);

    /**
     * Checks whether text is a block-header prefix.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is a block-header prefix; otherwise {@code false}.
     */
    boolean isBlockHeaderPrefix(String text);

    /**
     * Checks whether text is a dependent block header.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is a dependent block header; otherwise {@code false}.
     */
    boolean isDependentBlockHeader(String text);

    /**
     * Checks whether text is an independent block header.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is an independent block header; otherwise {@code false}.
     */
    boolean isIndependentBlockHeader(String text);

    /**
     * Checks whether a token type represents a comment.
     *
     * @param tokenType lexer token type.
     * @return {@code true} when the token type is a comment; otherwise {@code false}.
     */
    boolean isComment(int tokenType);

    /**
     * Checks whether text is an inner-block prefix.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is an inner-block prefix; otherwise {@code false}.
     */
    boolean isInnerBlockPrefix(String text);

    /**
     * Checks whether text is a package-body inner block header.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is a package-body inner block header; otherwise {@code false}.
     */
    boolean isPackageBodyInnerBlockHeader(String text);
}
