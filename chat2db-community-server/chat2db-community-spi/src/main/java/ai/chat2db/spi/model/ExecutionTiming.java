package ai.chat2db.spi.model;

import ai.chat2db.community.domain.api.model.result.ExecutionMetrics;

import java.util.Objects;

/**
 * Centralizes execution timing lifecycle and duration quantization.
 */
public final class ExecutionTiming {

    private static final long NANOS_PER_MILLISECOND = 1_000_000L;

    private ExecutionTiming() {
    }

    public static long elapsedNanos(long startedAtNanos) {
        return Math.max(0L, System.nanoTime() - startedAtNanos);
    }

    public static long addNanos(long leftNanos, long rightNanos) {
        long safeLeftNanos = Math.max(0L, leftNanos);
        long safeRightNanos = Math.max(0L, rightNanos);
        return safeLeftNanos > Long.MAX_VALUE - safeRightNanos
                ? Long.MAX_VALUE
                : safeLeftNanos + safeRightNanos;
    }

    public static long subtractNanos(long totalNanos, long excludedNanos) {
        long safeTotalNanos = Math.max(0L, totalNanos);
        long safeExcludedNanos = Math.max(0L, excludedNanos);
        return safeExcludedNanos >= safeTotalNanos ? 0L : safeTotalNanos - safeExcludedNanos;
    }

    public static ExecutionMetrics started(long startedAtEpochMs) {
        return ExecutionMetrics.builder()
                .startedAtEpochMs(startedAtEpochMs)
                .build();
    }

    public static ExecutionMetrics finished(ExecutionMetrics executionMetrics) {
        Objects.requireNonNull(executionMetrics, "executionMetrics");
        executionMetrics.setFinishedAtEpochMs(System.currentTimeMillis());
        return executionMetrics;
    }

    public static long elapsedEpochMillis(ExecutionMetrics executionMetrics) {
        Objects.requireNonNull(executionMetrics, "executionMetrics");
        long startedAtEpochMs = Objects.requireNonNull(
                executionMetrics.getStartedAtEpochMs(), "executionMetrics.startedAtEpochMs");
        long finishedAtEpochMs = Objects.requireNonNull(
                executionMetrics.getFinishedAtEpochMs(), "executionMetrics.finishedAtEpochMs");
        if (finishedAtEpochMs <= startedAtEpochMs) {
            return 0L;
        }
        long elapsedMillis = finishedAtEpochMs - startedAtEpochMs;
        return elapsedMillis < 0L ? Long.MAX_VALUE : elapsedMillis;
    }

    public static ExecutionMetrics complete(ExecutionMetrics executionMetrics, long executeDurationNanos,
                                            long fetchDurationNanos, int fetchedRowCount) {
        Objects.requireNonNull(executionMetrics, "executionMetrics");
        long safeExecuteDurationNanos = Math.max(0L, executeDurationNanos);
        long totalDurationNanos = addNanos(safeExecuteDurationNanos, fetchDurationNanos);
        long totalDurationMs = nanosToMillis(totalDurationNanos);
        long executeDurationMs = nanosToMillis(safeExecuteDurationNanos);

        finished(executionMetrics);
        executionMetrics.setTotalDurationMs(totalDurationMs);
        executionMetrics.setExecuteDurationMs(executeDurationMs);
        executionMetrics.setFetchDurationMs(totalDurationMs - executeDurationMs);
        executionMetrics.setFetchedRowCount(Math.max(0, fetchedRowCount));
        return executionMetrics;
    }

    private static long nanosToMillis(long durationNanos) {
        return Math.max(0L, durationNanos) / NANOS_PER_MILLISECOND;
    }
}
