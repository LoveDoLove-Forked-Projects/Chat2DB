package ai.chat2db.plugin.redis.config;

public final class RedisScanConfig {

    private static final int DEFAULT_COUNT = 1000;
    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 1000;
    private static final int MAX_SCAN_CALLS_PER_REQUEST = 10;
    private static final int KEY_TARGET_PER_REQUEST = 500;
    private static final int TABLE_SCAN_COUNT = 1000;
    private static final int LEGACY_MATCH_COUNT = 50;
    private static final int LEGACY_TOP_COUNT = 20;

    public static final RedisScanConfig DEFAULT = new RedisScanConfig(DEFAULT_COUNT, MIN_COUNT, MAX_COUNT,
            MAX_SCAN_CALLS_PER_REQUEST, KEY_TARGET_PER_REQUEST, TABLE_SCAN_COUNT, LEGACY_MATCH_COUNT,
            LEGACY_TOP_COUNT);

    private final int defaultCount;
    private final int minCount;
    private final int maxCount;
    private final int maxScanCallsPerRequest;
    private final int keyTargetPerRequest;
    private final int tableScanCount;
    private final int legacyMatchCount;
    private final int legacyTopCount;

    public RedisScanConfig(int defaultCount, int minCount, int maxCount, int maxScanCallsPerRequest,
            int keyTargetPerRequest, int tableScanCount, int legacyMatchCount, int legacyTopCount) {
        if (minCount < 1) {
            throw new IllegalArgumentException("minCount must be positive");
        }
        if (maxCount < minCount) {
            throw new IllegalArgumentException("maxCount must be greater than or equal to minCount");
        }
        if (defaultCount < minCount || defaultCount > maxCount) {
            throw new IllegalArgumentException("defaultCount must be within minCount and maxCount");
        }
        if (maxScanCallsPerRequest < 1) {
            throw new IllegalArgumentException("maxScanCallsPerRequest must be positive");
        }
        if (keyTargetPerRequest < 1) {
            throw new IllegalArgumentException("keyTargetPerRequest must be positive");
        }
        if (tableScanCount < 1) {
            throw new IllegalArgumentException("tableScanCount must be positive");
        }
        if (legacyMatchCount < 1) {
            throw new IllegalArgumentException("legacyMatchCount must be positive");
        }
        if (legacyTopCount < 1) {
            throw new IllegalArgumentException("legacyTopCount must be positive");
        }
        this.defaultCount = defaultCount;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.maxScanCallsPerRequest = maxScanCallsPerRequest;
        this.keyTargetPerRequest = keyTargetPerRequest;
        this.tableScanCount = tableScanCount;
        this.legacyMatchCount = legacyMatchCount;
        this.legacyTopCount = legacyTopCount;
    }

    public int defaultCount() {
        return defaultCount;
    }

    public int minCount() {
        return minCount;
    }

    public int maxCount() {
        return maxCount;
    }

    public int maxScanCallsPerRequest() {
        return maxScanCallsPerRequest;
    }

    public int keyTargetPerRequest() {
        return keyTargetPerRequest;
    }

    public int tableScanCount() {
        return tableScanCount;
    }

    public int legacyMatchCount() {
        return legacyMatchCount;
    }

    public int legacyTopCount() {
        return legacyTopCount;
    }
}
