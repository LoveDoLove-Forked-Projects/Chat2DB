package ai.chat2db.community.domain.api.model.parser.message;


import lombok.Data;

@Data
public class SyntaxErrorMessage {
    private int errorStartLine;
    private int errorStartPositionInLine;
    private int errorEndPositionInLine;
    private int errorEndLine;
    private String errorTokenText;
    private String errorMessage;

    public SyntaxErrorMessage() {
    }

    public SyntaxErrorMessage(int errorLine, int errorStartPositionInLine, int errorEndPositionInLine, String errorTokenText, String errorMessage) {
        this.errorStartLine = errorLine;
        this.errorStartPositionInLine = errorStartPositionInLine;
        this.errorEndPositionInLine = errorEndPositionInLine;
        this.errorEndLine = errorLine;
        this.errorTokenText = errorTokenText;
        this.errorMessage = errorMessage;
    }

}
