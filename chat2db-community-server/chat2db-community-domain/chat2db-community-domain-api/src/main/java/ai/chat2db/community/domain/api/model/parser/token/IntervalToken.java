package ai.chat2db.community.domain.api.model.parser.token;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;


public class IntervalToken extends CommonToken {

    private int endLine;
    private int endColumn;

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    public IntervalToken (Token FirstToken, Token LastToken) {
        super(FirstToken);
        this.endLine = LastToken.getLine();
        this.endColumn = LastToken.getCharPositionInLine() + LastToken.getText().length();
    }

}

