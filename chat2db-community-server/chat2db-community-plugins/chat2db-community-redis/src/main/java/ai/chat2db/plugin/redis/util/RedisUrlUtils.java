package ai.chat2db.plugin.redis.util;

import ai.chat2db.plugin.redis.constant.RedisConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RedisUrlUtils {

    private static final Pattern REDIS_DATABASE_IN_URL_PATTERN = Pattern.compile("/([^/?#]*)");

    private RedisUrlUtils() {
    }

    public static String getDatabaseFromUrl(String url) {
        if (StringUtils.isBlank(url) || !StringUtils.startsWithIgnoreCase(url, RedisConstants.JDBC_REDIS_URL_PREFIX)) {
            return null;
        }
        Matcher matcher = REDIS_DATABASE_IN_URL_PATTERN.matcher(url.substring(RedisConstants.JDBC_REDIS_URL_PREFIX.length()));
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
