package ai.chat2db.spi.model;

import ai.chat2db.community.domain.api.model.result.ExecutionMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutionTimingTest {

    @Test
    void completeQuantizesOnceAndConservesThePublicDuration() {
        ExecutionMetrics metrics = ExecutionTiming.started(1L);

        ExecutionMetrics completed = ExecutionTiming.complete(metrics, 1_600_000L, 400_000L, 3);

        assertSame(metrics, completed);
        assertEquals(2L, completed.getTotalDurationMs());
        assertEquals(1L, completed.getExecuteDurationMs());
        assertEquals(1L, completed.getFetchDurationMs());
        assertEquals(completed.getTotalDurationMs(),
                completed.getExecuteDurationMs() + completed.getFetchDurationMs());
        assertEquals(3, completed.getFetchedRowCount());
        assertNotNull(completed.getFinishedAtEpochMs());
    }

    @Test
    void completeKeepsAnUpdateDurationInTheExecutePhase() {
        ExecutionMetrics completed = ExecutionTiming.complete(
                ExecutionTiming.started(1L), 1_600_000L, 0L, 0);

        assertEquals(completed.getTotalDurationMs(), completed.getExecuteDurationMs());
        assertEquals(0L, completed.getFetchDurationMs());
    }

    @Test
    void completeClampsNegativeValuesAndSaturatesOverflow() {
        ExecutionMetrics negative = ExecutionTiming.complete(
                ExecutionTiming.started(1L), -1L, -1L, -1);
        assertEquals(0L, negative.getTotalDurationMs());
        assertEquals(0L, negative.getExecuteDurationMs());
        assertEquals(0L, negative.getFetchDurationMs());
        assertEquals(0, negative.getFetchedRowCount());

        ExecutionMetrics overflow = ExecutionTiming.complete(
                ExecutionTiming.started(1L), Long.MAX_VALUE, Long.MAX_VALUE, 0);
        assertEquals(Long.MAX_VALUE / 1_000_000L, overflow.getTotalDurationMs());
        assertEquals(overflow.getTotalDurationMs(),
                overflow.getExecuteDurationMs() + overflow.getFetchDurationMs());
    }

    @Test
    void nanosArithmeticIsNonNegativeAndOverflowSafe() {
        long startedAtNanos = System.nanoTime();

        assertTrue(ExecutionTiming.elapsedNanos(startedAtNanos) >= 0L);
        assertEquals(0L, ExecutionTiming.addNanos(-1L, -1L));
        assertEquals(Long.MAX_VALUE, ExecutionTiming.addNanos(Long.MAX_VALUE, 1L));
        assertEquals(5L, ExecutionTiming.subtractNanos(8L, 3L));
        assertEquals(0L, ExecutionTiming.subtractNanos(3L, 8L));
        assertEquals(0L, ExecutionTiming.subtractNanos(-1L, 1L));
    }

    @Test
    void failedLifecycleFinishesWithoutCreatingPhaseMetrics() {
        ExecutionMetrics metrics = ExecutionTiming.started(System.currentTimeMillis());

        ExecutionMetrics finished = ExecutionTiming.finished(metrics);

        assertSame(metrics, finished);
        assertNotNull(finished.getFinishedAtEpochMs());
        assertTrue(ExecutionTiming.elapsedEpochMillis(finished) >= 0L);
        assertNull(finished.getTotalDurationMs());
        assertNull(finished.getExecuteDurationMs());
        assertNull(finished.getFetchDurationMs());
        assertNull(finished.getFetchedRowCount());
    }
}
