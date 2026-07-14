package ai.chat2db.plugin.mysql.parser.rule.predicate;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.plugin.mysql.parser.rule.MysqlRulePredicate;
import ai.chat2db.spi.util.TokenUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;

public class MysqlCreateProcedureBlockPredicate extends MysqlRulePredicate {


    public MysqlCreateProcedureBlockPredicate(int lookAheadTokens) {
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
        int offset = 2;
        int valuableTokenFound = 0;

        while (valuableTokenFound < lookaheadTokens) {
            Token token = tokenStream.LT(offset);
            if (token.getType() == Token.EOF) {
                return false;
            }
            if (TokenUtil.hasValuableText(token)) {
                int tokenType = token.getType();
                if (tokenType == MySqlLexer.BEGIN) {
                    return true;
                } else if (tokenType == MySqlLexer.SEMI) {
                    return false;
                }

                valuableTokenFound++;
            }
            offset++;
        }
        return false;
    }

}
