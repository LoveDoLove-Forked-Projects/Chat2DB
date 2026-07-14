package ai.chat2db.community.domain.api.enums.sql;

import lombok.Getter;

@Getter
public enum SqlSplitCommentStateEnum {
    NONE(0),
    CONDITIONAL(1),
    HINT(2);

    private final int value;

    SqlSplitCommentStateEnum(int value) {
        this.value = value;
    }

}
