package ai.chat2db.community.domain.api.enums.value;

public enum LobUnitEnum {
    B("B", 1L),
    K("KB", 1024L),
    M("MB", 1024L * 1024L),
    G("GB", 1024L * 1024L * 1024L);

    private final String unit;
    private final long size;

    LobUnitEnum(String unit, long size) {
        this.unit = unit;
        this.size = size;
    }

    public String getUnit() {
        return unit;
    }

    public long getSize() {
        return size;
    }
}
