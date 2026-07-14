package ai.chat2db.plugin.redis.util;

import ai.chat2db.plugin.redis.constant.RedisConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public final class RedisScanUtils {

    private RedisScanUtils() {
    }

    public static String normalizeCursor(String cursor) {
        if (StringUtils.isNumeric(cursor)) {
            return cursor;
        }
        return RedisConstants.SCAN_INITIAL_CURSOR;
    }

    public static int normalizeCount(Integer count, int defaultCount, int minCount, int maxCount) {
        if (count == null) {
            return defaultCount;
        }
        if (count < minCount) {
            return minCount;
        }
        return Math.min(count, maxCount);
    }

    public static String buildContainsMatchPattern(String searchKey) {
        if (StringUtils.isBlank(searchKey)) {
            return "*";
        }
        return "*" + escapeGlob(searchKey.trim()) + "*";
    }

    public static List<?> getKeys(Object scanKeysValue) {
        if (scanKeysValue instanceof List) {
            return (List<?>) scanKeysValue;
        }
        return Collections.emptyList();
    }

    public static String escapeGlob(String value) {
        StringBuilder builder = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (c == '\\' || c == '*' || c == '?' || c == '[' || c == ']') {
                builder.append('\\');
            }
            builder.append(c);
        }
        return builder.toString();
    }
}
