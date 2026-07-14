package ai.chat2db.community.domain.api.enums;

import ai.chat2db.community.tools.enums.IBaseEnum;
import lombok.Getter;


@Getter
public enum ExportSizeEnum implements IBaseEnum<String> {


    CURRENT_PAGE("CURRENT_PAGE"),


    ALL("ALL"),

    ;

    final String description;

    ExportSizeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
