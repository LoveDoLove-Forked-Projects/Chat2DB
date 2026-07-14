package ai.chat2db.plugin.mysql.parser.rule.predicate;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.plugin.mysql.parser.rule.MysqlRulePredicate;
import ai.chat2db.spi.util.TokenUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;


public class MysqlIfBlockPredicate extends MysqlRulePredicate {

    public MysqlIfBlockPredicate(int lookAheadTokens) {
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
            valuableTokenFound++;
            String text = token.getText().trim();
            if ("THEN".equalsIgnoreCase(text)) {
                return true;
            } else if ("NOT".equalsIgnoreCase(text) && valuableTokenFound == 1) {
                return false;
            } else if ("EXISTS".equalsIgnoreCase(text) && valuableTokenFound == 1) {
                return false;
            } else if (!"(".equalsIgnoreCase(text) && valuableTokenFound == 1) {
                return true;
            }
            lookaheadIndex++;

        }
        return false;
    }

    @Override
    public boolean matches(TokenStream tokenStream) {
        int valuableTokenFound = 0;
        int offset = 2;
        int encounteredTokenFound = 0;
        boolean foundExists = false;

        while (valuableTokenFound < lookaheadTokens) {
            Token token = tokenStream.LT(offset);
            int tokenType = token.getType();

            if (tokenType == Token.EOF) {
                return false;
            }

            if (!TokenUtil.hasValuableText(token)) {
                offset++;
                continue;
            }

            valuableTokenFound++;
            if (tokenType == MySqlLexer.THEN) {
                return true;
            }
            else if (tokenType == MySqlLexer.EXISTS && (valuableTokenFound == 1 || valuableTokenFound == 2)) {
                encounteredTokenFound = valuableTokenFound;
                foundExists = true;
            } else if (valuableTokenFound == 1 && tokenType == MySqlLexer.NOT) {
                encounteredTokenFound = valuableTokenFound;
            }
            else if (foundExists && (encounteredTokenFound + 1 == valuableTokenFound)) {
                return tokenType == MySqlLexer.LR_BRACKET;
            }
            else if (tokenType != MySqlLexer.LR_BRACKET && valuableTokenFound == 1) {
                return true;
            }

            offset++;
        }

        return false;
    }

}
