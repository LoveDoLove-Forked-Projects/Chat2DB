package ai.chat2db.community.storage;

import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.util.ContextUtils;

public class IdUtil {

    public static Long generateId() {
        Long userId = null;
        Context context = ContextUtils.queryContext();
        if (context != null && context.getLoginUser() != null) {
            userId = context.getLoginUser().getId();
        }
        if (userId == null) {
            userId = 0L;
        }
        String id = System.currentTimeMillis() + "" + Math.floorMod(userId, 1000);
        return Long.parseLong(id);
    }

}
