package ai.chat2db.spi;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;

/**
 * Predicate used by dialect parsers to recognize token patterns.
 */
public interface IRulePredicate {

    /**
     * Checks whether a token list matches the rule at the current index.
     *
     * @param tokens tokens to inspect.
     * @param currentIndex index of the current token.
     * @return {@code true} when the rule matches; otherwise {@code false}.
     */
    boolean matches(List<Token> tokens, int currentIndex);

    /**
     * Checks whether a matching token list requires parser block-level handling.
     *
     * @param tokens tokens to inspect.
     * @param currentIndex index of the current token.
     * @return {@code true} when the parser should upgrade the current statement to a block; otherwise {@code false}.
     */
    boolean needUpgradeBlock(List<Token> tokens, int currentIndex);

    /**
     * Checks whether the current token stream position matches the rule.
     *
     * @param tokenStream token stream positioned by the parser.
     * @return {@code true} when the rule matches the stream position; otherwise {@code false}.
     */
    boolean matches(TokenStream tokenStream);
}
