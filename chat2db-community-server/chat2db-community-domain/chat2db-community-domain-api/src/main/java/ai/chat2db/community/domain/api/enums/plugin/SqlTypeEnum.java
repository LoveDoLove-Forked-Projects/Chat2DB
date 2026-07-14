package ai.chat2db.community.domain.api.enums.plugin;

import ai.chat2db.community.tools.enums.IBaseEnum;
import lombok.Getter;


@Getter
public enum SqlTypeEnum implements IBaseEnum<String> {



    SELECT("Check for phrases"),




    UNKNOWN("unknow"),

    ;

    final String description;

    SqlTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
