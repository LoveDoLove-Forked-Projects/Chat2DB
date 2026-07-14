package ai.chat2db.plugin.postgresql.parser.rule.predicate;

import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLLexer;
import ai.chat2db.plugin.postgresql.parser.rule.PgsqlRulePredicate;
import ai.chat2db.spi.util.TokenUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;

public class PgsqlCreateProcedureBlockPredicate extends PgsqlRulePredicate {


    public PgsqlCreateProcedureBlockPredicate(int lookAheadTokens) {
        this.lookaheadTokens = lookAheadTokens;
    }


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
            String text = token.getText().trim();
            if ("BEGIN".equalsIgnoreCase(text)) {
                return true;
            } else if (";".equalsIgnoreCase(text)) {
                return false;
            }
            lookaheadIndex++;

            valuableTokenFound++;
        }
        return false;

    }

    @Override
    public boolean matches(TokenStream tokenStream) {
        int offset = 1;
        int valuableTokenFound = 0;

        while (valuableTokenFound < lookaheadTokens) {
            Token token = tokenStream.LT(offset);
            int tokenType = token.getType();
            if (tokenType == Token.EOF) {
                return false;
            }
            if (TokenUtil.hasValuableText(token)) {
                if (tokenType == PostgreSQLLexer.BEGIN_P) {
                    return true;
                } else if (tokenType == PostgreSQLLexer.SEMI) {
                    return false;
                }

                valuableTokenFound++;
            }
            offset++;
        }
        return false;
    }
}
