package ai.chat2db.spi.parser.error.listener;

import ai.chat2db.community.domain.api.model.parser.message.SyntaxErrorMessage;
import lombok.Data;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class BaseSyntaxErrorListener extends BaseErrorListener {

    private List<SyntaxErrorMessage> errorMessages = new ArrayList<>(10);

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        Token errorToken = null;
        if (Objects.nonNull(offendingSymbol)) {
            errorToken = (Token) offendingSymbol;
        }
        SyntaxErrorMessage syntaxErrorMessage = new SyntaxErrorMessage();
        syntaxErrorMessage.setErrorStartLine(line);
        syntaxErrorMessage.setErrorEndLine(line);
        syntaxErrorMessage.setErrorStartPositionInLine(charPositionInLine);
        syntaxErrorMessage.setErrorMessage(msg);
        if (Objects.nonNull(errorToken)) {
            String text = errorToken.getText();
            if (StringUtils.isNotBlank(text)) {
                syntaxErrorMessage.setErrorEndPositionInLine(charPositionInLine + text.length());
                syntaxErrorMessage.setErrorTokenText(text);
            }
        }
        errorMessages.add(syntaxErrorMessage);
    }
}
