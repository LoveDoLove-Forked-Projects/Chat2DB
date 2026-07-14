package ai.chat2db.community.tools.enums;

import ai.chat2db.community.tools.enums.IBaseEnum;
import lombok.Getter;


@Getter
public enum ModeEnum implements IBaseEnum<String> {


    DESKTOP("DESKTOP"),


    WEB("WEB"),

    ;
    final String description;

    ModeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
