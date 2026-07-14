package ai.chat2db.plugin.oscar.enums.type;

import ai.chat2db.plugin.oscar.constant.OscarConstants;
import ai.chat2db.community.domain.api.model.metadata.DefaultValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum OscarDefaultValueEnum {

    EMPTY_STRING(OscarConstants.EMPTY_STRING_TOKEN),
    NULL(OscarConstants.NULL_TOKEN),
    SYSDATE("SYSDATE"),
    CURRENT_TIMESTAMP("CURRENT_TIMESTAMP");

    private final DefaultValue defaultValue;

    OscarDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(OscarDefaultValueEnum.values())
                .map(OscarDefaultValueEnum::getDefaultValue)
                .toList();
    }
}
