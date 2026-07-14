package ai.chat2db.community.domain.api.enums;

import ai.chat2db.community.tools.enums.IBaseEnum;
import lombok.Getter;


@Getter
public enum RoleCodeEnum implements IBaseEnum<String> {


    DESKTOP("DESKTOP", 1L, "_desktop_default_user_name", "_desktop_default_user_name"),


    ADMIN("ADMIN", 2L, "chat2db", "chat2db"),


    USER("USER", null, null, null),

    ;
    final String description;
    final Long defaultUserId;
    final String userName;
    final String password;

    RoleCodeEnum(String description, Long defaultUserId, String userName, String password) {
        this.description = description;
        this.defaultUserId = defaultUserId;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
