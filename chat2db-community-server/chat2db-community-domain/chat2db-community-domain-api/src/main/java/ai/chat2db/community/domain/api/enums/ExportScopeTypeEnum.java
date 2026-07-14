package ai.chat2db.community.domain.api.enums;

import ai.chat2db.community.tools.enums.IBaseEnum;
import lombok.Getter;

@Getter
public enum ExportScopeTypeEnum implements IBaseEnum<String> {


    ALL("ALL"),


    SCHEMA("SCHEMA"),


    TABLE("TABLE"),

    ;

    final String description;

    ExportScopeTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    public static ExportScopeTypeEnum from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
