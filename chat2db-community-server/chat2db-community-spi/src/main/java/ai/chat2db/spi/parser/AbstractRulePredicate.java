package ai.chat2db.spi.parser;

import ai.chat2db.spi.IRulePredicate;

import ai.chat2db.spi.util.TokenUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;
import java.util.Set;

public abstract class AbstractRulePredicate implements IRulePredicate {

    protected int lookaheadTokens;
    protected int getValidTokenStart(List<Token> tokens, int currentIndex) {
        int index = currentIndex + 1;
        while (index < tokens.size() && index < currentIndex + lookaheadTokens) {
            Token token = tokens.get(index);
            if (TokenUtil.hasValuableText(token)) {
                return index;
            }
            index++;
        }

        return -1;
    }
    protected int getNthValidToken(List<Token> tokens, int currentIndex, int n) {
        int index = currentIndex + 1;
        int validTokenCount = 0;
        while (index < tokens.size() && validTokenCount < n) {
            Token token = tokens.get(index);
            if (TokenUtil.hasValuableText(token)) {
                validTokenCount++;
                if (validTokenCount == n) {
                    return index;
                }
            }
            index++;
        }

        return -1;
    }
    protected int getNthValidTokenWithMatch(List<Token> tokens, int currentIndex, int n, String targetContent) {
        int index = currentIndex + 1;
        int validTokenCount = 0;
        while (index < tokens.size() && validTokenCount < n) {
            Token token = tokens.get(index);
            if (TokenUtil.hasValuableText(token)) {
                validTokenCount++;
                if (validTokenCount == n && token.getText().equalsIgnoreCase(targetContent)) {
                    return index;
                }
            }
            index++;
        }

        return -1;
    }


    protected int getNthValidTokenWithMatchBackward(List<Token> tokens, int currentIndex, int n, String targetContent) {
        int index = currentIndex - 1;
        int validTokenCount = 0;
        while (index >= 0 && validTokenCount < n) {
            Token token = tokens.get(index);
            if (TokenUtil.hasValuableText(token)) {
                validTokenCount++;
                if (validTokenCount == n && token.getText().equalsIgnoreCase(targetContent)) {
                    return index;
                }
            }
            index--;
        }

        return -1;
    }

    protected int getNthValidTokenWithMatch(List<Token> tokens, int currentIndex, int n, Set<String> targetContents) {
        int index = currentIndex + 1;
        int validTokenCount = 0;
        while (index < tokens.size() && validTokenCount < n) {
            Token token = tokens.get(index);
            if (TokenUtil.hasValuableText(token)) {
                validTokenCount++;
                if (validTokenCount == n) {
                    for (String targetContent : targetContents) {
                        if (token.getText().equalsIgnoreCase(targetContent)) {
                            return index;
                        }
                    }
                }
            }
            index++;
        }

        return -1;
    }

    protected int getNthValidTokenWithMatch(TokenStream tokenStream,
                                            int n,
                                            Set<Integer> targetTokens) {
        int validTokenCount = 0;
        int offset = 2;

        while (validTokenCount < n) {
            Token token = tokenStream.LT(offset);
            if (token.getType() == Token.EOF) {
                return -1;
            }
            if (TokenUtil.hasValuableText(token)) {
                validTokenCount++;
                if (targetTokens.contains(token.getType())) {
                    return token.getTokenIndex();
                }
            }
            offset++;
        }
        return -1;
    }


}
