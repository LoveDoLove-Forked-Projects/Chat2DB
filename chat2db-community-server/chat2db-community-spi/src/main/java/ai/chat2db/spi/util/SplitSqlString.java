


package ai.chat2db.spi.util;

public class SplitSqlString {
    private int offset;
    private String str;

    public SplitSqlString() {
    }

    public SplitSqlString(int offset, String str) {
        this.offset = offset;
        this.str = str;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
