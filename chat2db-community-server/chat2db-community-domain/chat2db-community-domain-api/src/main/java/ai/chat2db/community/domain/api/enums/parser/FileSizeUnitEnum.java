package ai.chat2db.community.domain.api.enums.parser;


public enum FileSizeUnitEnum {
    KB(1024),
    MB(1024 * 1024);

    private final int bytes;

    FileSizeUnitEnum(int bytes) {
        this.bytes = bytes;
    }

    public int toBytes() {
        return bytes;
    }
}
