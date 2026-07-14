package ai.chat2db.spi.util;


import ai.chat2db.community.domain.api.model.parser.position.TokenPosition;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TokenUtil {


    public static List<Token> getParserRuleTokens(CommonTokenStream commonTokenStream, ParseTree parserTree) {
        return commonTokenStream.getTokens(parserTree.getSourceInterval().a, parserTree.getSourceInterval().b);
    }


    public static List<Token> getParserRuleTokensOnDefault(CommonTokenStream commonTokenStream, ParseTree parserTree) {
        return getParserRuleTokens(commonTokenStream, parserTree).stream()
                .filter(token -> token.getChannel() == Token.DEFAULT_CHANNEL)
                .toList();
    }


    public static List<Token> getParserRuleTokensOnDefault(List<Token> tokens) {
        return tokens.stream()
                .filter(token -> token.getChannel() == Token.DEFAULT_CHANNEL)
                .toList();
    }

    public static Map<TokenPosition, Token> getTokenPositionMap(List<Token> tokens) {
        Map<TokenPosition, Token> tokenPositionMap = new HashMap<>();
        for (Token token : tokens) {
            TokenPosition tokenPosition = new TokenPosition(token.getLine(), token.getCharPositionInLine());
            tokenPositionMap.put(tokenPosition, token);
        }
        return tokenPositionMap;
    }


    public static List<List<Token>> splitTokensBySymbolText(List<Token> tokens, String... symbolTexts) {
        List<List<Token>> tokenLists = new ArrayList<>();
        List<Token> subTokenList = new ArrayList<>();
        Set<String> symbols = new HashSet<>(Set.of(symbolTexts));

        for (Token token : tokens) {
            if (symbols.contains(token.getText())) {
                if (!subTokenList.isEmpty()) {
                    tokenLists.add(new ArrayList<>(subTokenList));
                    subTokenList.clear();
                }
            } else {
                subTokenList.add(token);
            }
        }
        if (!subTokenList.isEmpty()) {
            tokenLists.add(subTokenList);
        }

        return tokenLists;
    }


    public static String buildSqlByKeywordsAroundCursor(List<Token> beforeTokens, List<Token> afterTokens, Set<String> sqlKeyWords) {
        String beforeSql = buildSqlByKeywordsBeforeCursor(beforeTokens, sqlKeyWords);
        String afterSql = buildSqlByKeywordsAfterCursor(afterTokens, sqlKeyWords);
        return beforeSql + " " + afterSql;
    }


    public static String buildSqlByKeywordsBeforeCursor(List<Token> beforeTokens, Set<String> sqlKeywords) {
        StringBuilder sb = new StringBuilder(50);
        for (int i = beforeTokens.size() - 1; i >= 0; i--) {
            Token token = beforeTokens.get(i);
            String text = token.getText();
            String upperText = text.toUpperCase();
            if (sqlKeywords.contains(upperText)) {
                if (!StringUtils.equalsAnyIgnoreCase(";", "/", "go", text)) {
                    sb.insert(0, text);
                }
                break;
            }
            if (token.getType() != Token.EOF) {
                sb.insert(0, text);
            }

        }
        return sb.toString();
    }

    public static String buildSqlByKeywordsAfterCursor(List<Token> afterTokens, Set<String> sqlKeywords) {
        StringBuilder sb = new StringBuilder(50);
        for (Token token : afterTokens) {
            String text = token.getText();
            String upperText = text.toUpperCase();
            if (sqlKeywords.contains(upperText) || token.getType() == Token.EOF) {
                break;
            }
            sb.append(text);

        }
        return sb.toString();
    }

    public static boolean hasValuableText(Token token) {
        if (Objects.isNull(token)) {
            return false;
        }
        String text = token.getText();
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return !text.equals("\n") && !text.equals("\r") && !text.equals("\r\n");
    }

    public static boolean noLetterDigit(Token token) {
        if (token == null) {
            return true;
        }
        String text = token.getText();
        if (StringUtils.isBlank(text)) {
            return true;
        }
        return text.matches("^[^a-zA-Z0-9]+$");
    }
}
