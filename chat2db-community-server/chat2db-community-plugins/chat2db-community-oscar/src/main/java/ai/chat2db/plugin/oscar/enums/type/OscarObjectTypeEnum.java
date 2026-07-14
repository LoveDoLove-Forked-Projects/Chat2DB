package ai.chat2db.plugin.oscar.enums.type;

import lombok.Getter;

@Getter
public enum OscarObjectTypeEnum {

    TABLE("TABLE"),
    VIEW("VIEW"),
    FUNCTION("FUNCTION"),
    PROCEDURE("PROCEDURE"),
    TRIGGER("TRIGGER");

    private final String code;

    OscarObjectTypeEnum(String code) {
        this.code = code;
    }

}
