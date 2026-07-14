package ai.chat2db.community.domain.api.enums;

import ai.chat2db.community.tools.enums.IBaseEnum;
import lombok.Getter;


@Getter
public enum DataSourceKindEnum implements IBaseEnum<String> {


    PRIVATE("PRIVATE"),


    SHARED("SHARED"),

    ;

    final String description;

    DataSourceKindEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
