package ai.chat2db.plugin.mysql.enums.type;

import ai.chat2db.community.domain.api.model.metadata.DefaultValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum MysqlDefaultValueEnum {

    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"),
    ;
    private DefaultValue defaultValue;

    MysqlDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(MysqlDefaultValueEnum.values()).map(MysqlDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
