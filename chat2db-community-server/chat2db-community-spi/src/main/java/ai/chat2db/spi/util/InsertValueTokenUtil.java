package ai.chat2db.spi.util;

import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.statement.insert.InsertRowTokenRange;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class InsertValueTokenUtil {

    private static final String VALUES_KEYWORD = "values";
    private static final String LEFT_PAREN = "(";
    private static final String RIGHT_PAREN = ")";
    private static final String STATEMENT_END = ";";

    private InsertValueTokenUtil() {
    }

    public static List<InsertRowTokenRange> buildRowTokenRanges(List<? extends TerminalNode> rowStartNodes,
                                                                List<? extends TerminalNode> rowEndNodes) {
        List<InsertRowTokenRange> rowTokenRanges = new ArrayList<>();
        if (Objects.isNull(rowStartNodes) || rowStartNodes.isEmpty()) {
            return rowTokenRanges;
        }
        for (int rowIndex = 0; rowIndex < rowStartNodes.size(); rowIndex++) {
            Token rowFirstToken = rowStartNodes.get(rowIndex).getSymbol();
            Token rowLastToken = Objects.nonNull(rowEndNodes) && rowEndNodes.size() > rowIndex
                    ? rowEndNodes.get(rowIndex).getSymbol() : rowFirstToken;
            rowTokenRanges.add(new InsertRowTokenRange(rowFirstToken, rowLastToken));
        }
        return rowTokenRanges;
    }

    public static List<InsertRowTokenRange> singleRowTokenRange(Token rowFirstToken, Token rowLastToken) {
        if (Objects.isNull(rowFirstToken)) {
            return List.of();
        }
        return List.of(new InsertRowTokenRange(rowFirstToken, Objects.nonNull(rowLastToken) ? rowLastToken : rowFirstToken));
    }

    public static List<InsertRowTokenRange> searchValuesRowTokenRanges(StatementContext context, Token startToken,
                                                                       Token stopToken) {
        List<InsertRowTokenRange> rowTokenRanges = new ArrayList<>();
        TokenStream tokenStream = Objects.nonNull(context) ? context.getCommonTokenStream() : null;
        if (Objects.isNull(tokenStream) || Objects.isNull(startToken) || Objects.isNull(stopToken)
                || startToken.getTokenIndex() < 0 || stopToken.getTokenIndex() < startToken.getTokenIndex()) {
            return rowTokenRanges;
        }

        boolean afterValues = false;
        int depth = 0;
        Token rowFirstToken = null;
        for (int tokenIndex = startToken.getTokenIndex(); tokenIndex <= stopToken.getTokenIndex(); tokenIndex++) {
            Token token = tokenStream.get(tokenIndex);
            if (Objects.isNull(token) || token.getChannel() != Token.DEFAULT_CHANNEL) {
                continue;
            }
            String text = token.getText();
            if (!afterValues) {
                if (StringUtils.equalsIgnoreCase(text, VALUES_KEYWORD)) {
                    afterValues = true;
                }
                continue;
            }
            if (StringUtils.equals(text, LEFT_PAREN)) {
                if (depth == 0) {
                    rowFirstToken = token;
                }
                depth++;
            } else if (StringUtils.equals(text, RIGHT_PAREN) && depth > 0) {
                depth--;
                if (depth == 0 && Objects.nonNull(rowFirstToken)) {
                    rowTokenRanges.add(new InsertRowTokenRange(rowFirstToken, token));
                    rowFirstToken = null;
                }
            } else if (depth == 0 && StringUtils.equals(text, STATEMENT_END)) {
                break;
            }
        }
        return rowTokenRanges;
    }
}
