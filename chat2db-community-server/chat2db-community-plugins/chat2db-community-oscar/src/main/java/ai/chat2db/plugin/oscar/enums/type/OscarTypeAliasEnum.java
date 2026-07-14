package ai.chat2db.plugin.oscar.enums.type;

import org.apache.commons.lang3.StringUtils;

public enum OscarTypeAliasEnum {

    INT2("INT2", "SMALLINT"),
    INT4("INT4", "INT"),
    INT8("INT8", "BIGINT");

    private final String nativeType;
    private final String normalizedType;

    OscarTypeAliasEnum(String nativeType, String normalizedType) {
        this.nativeType = nativeType;
        this.normalizedType = normalizedType;
    }

    public static String normalize(String typeName) {
        for (OscarTypeAliasEnum value : OscarTypeAliasEnum.values()) {
            if (StringUtils.equalsIgnoreCase(value.nativeType, typeName)) {
                return value.normalizedType;
            }
        }
        return typeName;
    }
}
