package ai.chat2db.community.domain.core.impl.task.imports;

import ai.chat2db.community.domain.api.model.task.ArtifactDraft;
import ai.chat2db.community.domain.api.service.task.TaskCancelable;
import ai.chat2db.community.domain.api.service.task.TaskExecutionContext;
import org.junit.jupiter.api.Test;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportProgressReporterTest {

    @Test
    void knownTotalProgressIsLinearAndStopsBeforeCompletion() {
        RecordingContext context = new RecordingContext();
        ImportProgressReporter reporter = new ImportProgressReporter(context, 1_000L);

        reporter.report(100L);
        reporter.report(500L);
        reporter.report(1_000L);

        assertEquals(List.of(27, 57, 95), context.progressValues());
        assertEquals(List.of(1_000L, 1_000L, 1_000L), context.reportedTotals());
        assertEquals(List.of(100L, 500L, 1_000L), context.reportedRows());
        assertTrue(context.messages().get(2).startsWith("Imported 1000 of 1000 rows"),
                context.messages().get(2));
        assertEquals("IMPORTING", context.stages().get(2));
    }

    @Test
    void unknownTotalProgressIsMonotonicAndKeepsRowsInTheMessage() {
        RecordingContext context = new RecordingContext();
        ImportProgressReporter reporter = new ImportProgressReporter(context);

        reporter.report(1_000L);
        int first = context.progressValues().get(0);
        reporter.report(500L);
        reporter.report(1_000L);
        reporter.report(1_000_000L);

        assertEquals(2, context.progressValues().size(), "a lower or repeated row count is ignored");
        assertEquals(47, first);
        assertTrue(context.progressValues().get(1) > first, "progress never decreases");
        assertTrue(context.progressValues().get(1) < 95,
                "an import with an unknown total must not look almost complete");
        assertEquals(List.of(1_000L, 1_000_000L), context.reportedRows());
        assertTrue(context.messages().get(0).startsWith("Imported 1000 rows (total unknown)"),
                context.messages().get(0));
        assertEquals(reporter.lastProgress(), context.progressValues().get(1));
    }

    private static final class RecordingContext implements TaskExecutionContext {

        private final List<Integer> progressValues = new ArrayList<>();

        private final List<String> stages = new ArrayList<>();

        private final List<String> messages = new ArrayList<>();

        @Override
        public void reportProgress(int progress, String stage, String message) {
            progressValues.add(progress);
            stages.add(stage);
            messages.add(message);
        }

        private List<Integer> progressValues() {
            return progressValues;
        }

        private List<String> stages() {
            return stages;
        }

        private List<String> messages() {
            return messages;
        }

        private List<Long> reportedRows() {
            return messages.stream().map(message -> Long.parseLong(message.split(" ")[1])).toList();
        }

        private List<Long> reportedTotals() {
            return messages.stream().map(message -> Long.parseLong(message.split(" ")[3])).toList();
        }

        @Override
        public void logInfo(String code, String message) {
        }

        @Override
        public void logInfo(String code, String message, Map<String, Object> details) {
        }

        @Override
        public void logWarn(String code, String message, Map<String, Object> details) {
        }

        @Override
        public void logError(String code, String message, Map<String, Object> details) {
        }

        @Override
        public void checkCancelled() {
        }

        @Override
        public void registerCancelable(TaskCancelable resource) {
        }

        @Override
        public ArtifactDraft createArtifact(String outputDirectory, String fileName, String mediaType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(String content) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onStatementCreated(Statement statement) {
        }

        @Override
        public void onStatementClosed(Statement statement) {
        }
    }
}
