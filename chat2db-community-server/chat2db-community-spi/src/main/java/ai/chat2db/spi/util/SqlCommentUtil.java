package ai.chat2db.spi.util;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

public class SqlCommentUtil {
    public static String searchSqlComment(int start, int end, TokenStream commonTokenStream) {

        if (start == 0 && end == 1) {
            Token token = commonTokenStream.get(start);
            if (token.getChannel() == Token.HIDDEN_CHANNEL) {
                String text = token.getText().trim();
                if (text.startsWith("--")) {
                    return text.substring(2).trim();
                }
                if (text.startsWith("#")) {
                    return text.substring(1).trim();
                }
            }
            return null;
        }
        for (int j = end - 1; j > start; j--) {
            Token token = commonTokenStream.get(j);
            if (token.getChannel() == Token.HIDDEN_CHANNEL) {
                String text = token.getText().trim();
                if (text.startsWith("--")) {
                    return text.substring(2).trim();
                }
                if (text.startsWith("#")) {
                    return text.substring(1).trim();
                }
            }
        }
        return null;
    }

}
