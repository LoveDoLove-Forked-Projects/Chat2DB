package ai.chat2db.plugin.sundb.enums.type;

import lombok.Getter;

@Getter
public enum SUNDBObjectTypeEnum {

    FUNCTION("FUNCTION"),
    PROCEDURE("PROCEDURE"),
    VIEW("VIEW"),
    ;

    private String objectType;
    SUNDBObjectTypeEnum(String objectType) {
        this.objectType = objectType;
    }
}
