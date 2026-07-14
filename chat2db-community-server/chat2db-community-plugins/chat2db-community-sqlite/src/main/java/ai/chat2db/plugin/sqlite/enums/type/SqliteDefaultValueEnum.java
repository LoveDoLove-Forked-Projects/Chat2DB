package ai.chat2db.plugin.sqlite.enums.type;

import ai.chat2db.community.domain.api.model.metadata.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum SqliteDefaultValueEnum {
    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    ;
    private DefaultValue defaultValue;

    SqliteDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(SqliteDefaultValueEnum.values()).map(SqliteDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
