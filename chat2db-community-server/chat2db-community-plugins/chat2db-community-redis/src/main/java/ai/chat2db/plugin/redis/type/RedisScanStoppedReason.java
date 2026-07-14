package ai.chat2db.plugin.redis.type;

public enum RedisScanStoppedReason {

    REDIS_CURSOR_COMPLETE,
    CLIENT_LIMIT_REACHED,
    COMMAND_BUDGET_REACHED
}
