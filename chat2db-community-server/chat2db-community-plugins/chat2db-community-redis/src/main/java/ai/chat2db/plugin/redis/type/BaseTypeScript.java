package ai.chat2db.plugin.redis.type;

import ai.chat2db.plugin.redis.RedisScriptExecutor;
import ai.chat2db.plugin.redis.constant.RedisConstants;

import java.sql.Connection;

import static ai.chat2db.plugin.redis.util.RedisValueUtils.getRedisValue;

public abstract class BaseTypeScript implements ITypeScript {

    public boolean existKey(Connection connection, String name) {
        return RedisScriptExecutor.getInstance().existKey(connection, name);
    }


    public String delete(String keyName) {
        return RedisConstants.COMMAND_DELETE_KEY_PREFIX + getRedisValue(keyName) + RedisConstants.COMMAND_LINE_SEPARATOR;
    }
}
