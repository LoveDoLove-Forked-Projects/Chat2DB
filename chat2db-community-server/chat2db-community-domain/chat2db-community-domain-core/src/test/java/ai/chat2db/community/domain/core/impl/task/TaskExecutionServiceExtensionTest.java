package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.enums.TaskTypeEnum;
import ai.chat2db.community.domain.api.model.task.extension.TaskExecutionContext;
import ai.chat2db.community.domain.api.model.task.extension.TaskOperation;
import ai.chat2db.community.domain.api.service.task.extension.ITaskExecutionGuard;
import ai.chat2db.community.domain.core.converter.ConnectionContextConverter;
import ai.chat2db.community.domain.core.impl.task.extension.TaskExtensionManager;
import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.util.ContextUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskExecutionServiceExtensionTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void cleanUpContext() {
        ContextUtils.removeContext();
    }

    @Test
    void taskGuardRunsBeforeWorkerCanCreateOutputFile() {
        Path output = tempDir.resolve("export.csv");
        List<String> events = new ArrayList<>();
        ITaskExecutionGuard guard = context -> {
            events.add("guard");
            assertFalse(Files.exists(output));
        };
        TaskExtensionManager extensionManager = new TaskExtensionManager(List.of(), List.of(guard));
        TaskExecutionServiceImpl service = new TaskExecutionServiceImpl(new ConnectionContextConverter(),
                extensionManager);

        Runnable worker = service.withConnectionProfile(null, null, taskContext(), () -> {
            events.add("worker");
            try {
                Files.writeString(output, "masked");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        worker.run();

        assertEquals(List.of("guard", "worker"), events);
        assertTrue(Files.exists(output));
    }

    @Test
    void nestedTaskExecutionRestoresOuterIdentityContext() {
        Context outerContext = Context.builder().organizationId(34L).token("outer").build();
        Context taskContext = Context.builder().organizationId(34L).token("task").build();
        ContextUtils.setContext(outerContext);
        TaskExecutionServiceImpl service = service(List.of());

        Runnable worker = service.withConnectionProfile(taskContext, null,
                () -> assertSame(taskContext, ContextUtils.queryContext()));
        worker.run();

        assertSame(outerContext, ContextUtils.queryContext());
    }

    @Test
    void topLevelTaskExecutionClearsIdentityContext() {
        Context taskContext = Context.builder().organizationId(34L).token("task").build();
        TaskExecutionServiceImpl service = service(List.of());

        service.withConnectionProfile(taskContext, null,
                () -> assertSame(taskContext, ContextUtils.queryContext())).run();

        assertNull(ContextUtils.queryContext());
    }

    private static TaskExecutionServiceImpl service(List<ITaskExecutionGuard> guards) {
        return new TaskExecutionServiceImpl(new ConnectionContextConverter(),
                new TaskExtensionManager(List.of(), guards));
    }

    private static TaskExecutionContext taskContext() {
        return new TaskExecutionContext(42L, TaskTypeEnum.DOWNLOAD_TABLE_DATA, null,
                "shop", null, List.of("orders"), TaskOperation.EXPORT);
    }
}
