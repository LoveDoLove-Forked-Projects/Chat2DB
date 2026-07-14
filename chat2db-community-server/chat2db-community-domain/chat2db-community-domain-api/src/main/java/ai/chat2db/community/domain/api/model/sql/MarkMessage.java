package ai.chat2db.community.domain.api.model.sql;

import ai.chat2db.community.domain.api.model.parser.message.SyntaxErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkMessage {
    private int endLineNum;
    private int startLineNum;
    private int startColNum;
    private int endColNum;
    private String message;
    private String type;

    public MarkMessage(SyntaxErrorMessage syntaxErrorMessage) {
        this.type = "error";
        this.startLineNum = syntaxErrorMessage.getErrorStartLine();
        this.startColNum = syntaxErrorMessage.getErrorStartPositionInLine();
        this.message = syntaxErrorMessage.getErrorMessage();
        this.endLineNum = syntaxErrorMessage.getErrorEndLine();
        this.endColNum = syntaxErrorMessage.getErrorEndPositionInLine();

    }
}
