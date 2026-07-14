package ai.chat2db.plugin.xugudb.enums.type;

import ai.chat2db.community.domain.api.model.metadata.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum XUGUDBDefaultValueEnum {
    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    ;
    private DefaultValue defaultValue;

    XUGUDBDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(XUGUDBDefaultValueEnum.values()).map(XUGUDBDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
