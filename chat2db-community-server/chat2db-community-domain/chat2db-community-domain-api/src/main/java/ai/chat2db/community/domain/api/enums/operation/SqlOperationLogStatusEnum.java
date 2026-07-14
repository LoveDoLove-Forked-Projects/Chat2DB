package ai.chat2db.community.domain.api.enums.operation;

import lombok.Getter;

@Getter
public enum SqlOperationLogStatusEnum {

    SUCCESS("success"),
    FAIL("fail"),
    CANCELLED("cancelled");

    private final String code;

    SqlOperationLogStatusEnum(String code) {
        this.code = code;
    }
}
