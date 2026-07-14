package ai.chat2db.community.domain.api.enums.plugin;

import lombok.Getter;

@Getter
public enum AuthenticationTypeEnum {

    USER_PASSWORD("1"),
    NONE("2"),
    PASSWORD("3");


    private final String code;

    AuthenticationTypeEnum(String code) {
        this.code = code;
    }

}
