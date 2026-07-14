package ai.chat2db.plugin.oracle.parser.rule.predicate;

import ai.chat2db.plugin.oracle.parser.base.PlSqlLexer;
import ai.chat2db.plugin.oracle.parser.rule.OracleRulePredicate;
import ai.chat2db.spi.util.TokenUtil;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@NoArgsConstructor
public class OracleCreateProcedurePredicate extends OracleRulePredicate {


    public OracleCreateProcedurePredicate(int lookAheadTokens) {
        this.lookaheadTokens = lookAheadTokens;
    }


    @Override
    public boolean matches(List<Token> tokens, int currentIndex) {
        if (lookaheadTokens == 0) {
            lookaheadTokens = tokens.size();
        }
        int lookaheadIndex = currentIndex + 1;
        int valuableTokenFound = 0;

        boolean encounteredIsOrAs = false;
        boolean previousIsLeftParen = getNthValidTokenWithMatchBackward(tokens, currentIndex, 1, "(") != -1;

        while (lookaheadIndex < tokens.size() && valuableTokenFound < lookaheadTokens) {
            Token token = tokens.get(lookaheadIndex);
            if (!TokenUtil.hasValuableText(token)) {
                lookaheadIndex++;
                continue;
            }
            lookaheadIndex++;
            valuableTokenFound++;
            String text = token.getText().trim();
            if (StringUtils.equalsAnyIgnoreCase(text, "IS", "AS")) {
                encounteredIsOrAs = true;
            } else if ("BEGIN".equalsIgnoreCase(text)) {
                return true;
            } else if (!encounteredIsOrAs && ";".equalsIgnoreCase(text)) {
                return false;
            } else if ("LANGUAGE".equalsIgnoreCase(text)) {
                return false;
            } else if ("EXTERNAL".equalsIgnoreCase(text)) {
                return false;
            } else if (previousIsLeftParen && ")".equalsIgnoreCase(text)) {
                return false;
            }

        }
        return false;

    }


    @Override
    public boolean matches(TokenStream tokenStream) {

        int offset = 2;
        int valuableTokenFound = 0;

        boolean encounteredIsOrAs = false;


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
            switch (tokenType) {
                case PlSqlLexer.IS:
                case PlSqlLexer.AS:
                    encounteredIsOrAs = true;
                    break;
                case PlSqlLexer.BEGIN:
                    return true;
                case PlSqlLexer.SEMICOLON:
                    if (!encounteredIsOrAs) {
                        return false;
                    }
                    break;
                case PlSqlLexer.LANGUAGE:
                case PlSqlLexer.EXTERNAL:
                    return false;
            }

            offset++;
        }
        return false;
    }


}
