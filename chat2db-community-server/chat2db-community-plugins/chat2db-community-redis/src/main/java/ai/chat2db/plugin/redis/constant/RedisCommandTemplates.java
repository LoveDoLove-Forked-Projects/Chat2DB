package ai.chat2db.plugin.redis.constant;

public final class RedisCommandTemplates {

    public static final String SCAN_MATCH_COUNT = "scan %s MATCH %s COUNT %s";

    private RedisCommandTemplates() {
    }
}
