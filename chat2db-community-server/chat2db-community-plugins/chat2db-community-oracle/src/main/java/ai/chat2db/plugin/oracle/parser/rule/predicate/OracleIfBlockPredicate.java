package ai.chat2db.plugin.oracle.parser.rule.predicate;

import ai.chat2db.plugin.oracle.parser.base.PlSqlLexer;
import ai.chat2db.plugin.oracle.parser.rule.OracleRulePredicate;
import ai.chat2db.spi.util.TokenUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;
import java.util.Set;

public class OracleIfBlockPredicate extends OracleRulePredicate {

    public OracleIfBlockPredicate(int lookAheadTokens) {
        this.lookaheadTokens = lookAheadTokens;
    }

    private static final Set<Integer> MATCH_TOKENS = Set.of(PlSqlLexer.NOT, PlSqlLexer.EXISTS);

    @Override
    public boolean matches(List<Token> tokens, int currentIndex) {
        int lookaheadIndex = currentIndex + 1;
        int valuableTokenFound = 0;

        while (lookaheadIndex < tokens.size() && valuableTokenFound < lookaheadTokens) {
            Token token = tokens.get(lookaheadIndex);
            if (!TokenUtil.hasValuableText(token)) {
                lookaheadIndex++;
                continue;
            }
            valuableTokenFound++;
            String text = token.getText().trim();
            if ("THEN".equalsIgnoreCase(text)) {
                return true;
            } else if ("NOT".equalsIgnoreCase(text) && valuableTokenFound == 1) {
                return false;
            } else if ("EXISTS".equalsIgnoreCase(text) && valuableTokenFound == 1) {
                return false;
            }
            lookaheadIndex++;

        }
        return false;
    }

    @Override
    public boolean matches(TokenStream tokenStream) {
        return getNthValidTokenWithMatch(tokenStream, 1, MATCH_TOKENS) == -1;
    }
}
