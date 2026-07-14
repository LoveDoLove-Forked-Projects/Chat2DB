package ai.chat2db.plugin.db2.enums.type;

import ai.chat2db.community.domain.api.model.metadata.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum DB2DefaultValueEnum {
    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    ;
    private DefaultValue defaultValue;

    DB2DefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(DB2DefaultValueEnum.values()).map(DB2DefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
