package ai.chat2db.community.domain.api.enums;

import ai.chat2db.community.tools.enums.IBaseEnum;
import lombok.Getter;


@Getter
public enum RoleCodeEnum implements IBaseEnum<String> {


    DESKTOP("DESKTOP", 1L),


    ADMIN("ADMIN", 2L),


    USER("USER", null),

    ;
    final String description;
    final Long defaultUserId;

    RoleCodeEnum(String description, Long defaultUserId) {
        this.description = description;
        this.defaultUserId = defaultUserId;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
