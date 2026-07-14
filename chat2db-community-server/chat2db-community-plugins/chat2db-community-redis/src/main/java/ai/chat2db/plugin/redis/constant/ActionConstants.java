package ai.chat2db.plugin.redis.constant;

import lombok.Getter;
import lombok.Setter;


public final class ActionConstants {

    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String CREATE = "add";

    private ActionConstants() {
    }
}
