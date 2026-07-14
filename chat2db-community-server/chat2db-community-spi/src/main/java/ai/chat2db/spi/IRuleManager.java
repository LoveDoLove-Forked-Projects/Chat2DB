package ai.chat2db.spi;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;
import java.util.Map;

/**
 * Provides parser rules and applies them to dialect token streams.
 */
public interface IRuleManager {

    /**
     * Returns named rule predicates registered for the dialect.
     *
     * @return rule map keyed by rule name.
     */
    Map<String, IRulePredicate> getRules();

    /**
     * Checks whether any registered rule matches a token list at the current index.
     *
     * @param tokens tokens to inspect.
     * @param currentTokenIndex index of the current token.
     * @return {@code true} when any rule matches; otherwise {@code false}.
     */
    boolean matchRules(List<Token> tokens, int currentTokenIndex);

    /**
     * Checks whether any registered rule requires block-level parsing at the current token.
     *
     * @param tokens tokens to inspect.
     * @param currentTokenIndex index of the current token.
     * @return {@code true} when block-level handling is required; otherwise {@code false}.
     */
    boolean upgradeBlockRules(List<Token> tokens, int currentTokenIndex);

    /**
     * Checks whether any registered rule matches the current token stream position.
     *
     * @param tokenStream token stream positioned by the parser.
     * @return {@code true} when any rule matches; otherwise {@code false}.
     */
    boolean matchRules(TokenStream tokenStream);
}
