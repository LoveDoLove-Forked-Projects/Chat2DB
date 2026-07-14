package ai.chat2db.plugin.postgresql.parser.rule.predicate;

import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLLexer;
import ai.chat2db.plugin.postgresql.parser.rule.PgsqlRulePredicate;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;
import java.util.Set;


public class PgsqlBeginBlockPredicate extends PgsqlRulePredicate {

    public PgsqlBeginBlockPredicate(int lookAheadTokens) {
        this.lookaheadTokens = lookAheadTokens;
    }

    public static final Set<Integer> MATCH_TOKENS = Set.of(PostgreSQLLexer.SEMI, PostgreSQLLexer.WORK, PostgreSQLLexer.TRANSACTION, PostgreSQLLexer.ISOLATION, PostgreSQLLexer.READ, PostgreSQLLexer.DEFERRABLE, PostgreSQLLexer.NOT);

    @Override
    public boolean matches(List<Token> tokens, int currentIndex) {
        return getNthValidTokenWithMatch(tokens, currentIndex, 1, Set.of(";", "WORK", "TRANSACTION", "ISOLATION", "READ", "DEFERRABLE", "NOT")) == -1;

    }

    @Override
    public boolean matches(TokenStream tokenStream) {
        return getNthValidTokenWithMatch(tokenStream, 1, MATCH_TOKENS) == -1;

    }
}
