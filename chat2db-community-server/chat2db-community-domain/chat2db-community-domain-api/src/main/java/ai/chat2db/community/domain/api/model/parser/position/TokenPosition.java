package ai.chat2db.community.domain.api.model.parser.position;

public class TokenPosition {
    private int rowNum;
    private int colNum;


    public TokenPosition(int rowNum, int colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TokenPosition that = (TokenPosition) obj;
        return rowNum == that.rowNum && colNum == that.colNum;
    }

    @Override
    public int hashCode() {
        int result = rowNum;
        result = 31 * result + colNum;
        return result;
    }
}
