package ai.chat2db.plugin.mysql.parser.rule.predicate;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.plugin.mysql.parser.rule.MysqlRulePredicate;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;
import java.util.Set;


public class MysqlBeginBlockPredicate extends MysqlRulePredicate {

    public MysqlBeginBlockPredicate(int lookAheadTokens) {
        this.lookaheadTokens = lookAheadTokens;
    }

    public static final Set<Integer> MATCH_TOKENS = Set.of(MySqlLexer.SEMI, MySqlLexer.WORK);

    @Override
    public boolean matches(List<Token> tokens, int currentIndex) {
        return getNthValidTokenWithMatch(tokens, currentIndex, 1, Set.of(";", "work")) == -1;

    }


    @Override
    public boolean matches(TokenStream tokenStream) {
        return getNthValidTokenWithMatch(tokenStream, 1, MATCH_TOKENS) == -1;
    }
}
