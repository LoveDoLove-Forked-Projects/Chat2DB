package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.model.request.task.TaskOtherFileExportRequest;
import ai.chat2db.community.domain.api.enums.TaskTypeEnum;
import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;
import ai.chat2db.community.domain.api.model.task.extension.TaskOperation;
import ai.chat2db.community.domain.api.model.task.extension.TaskSubmissionContext;
import ai.chat2db.community.domain.api.service.task.ITaskDataExportService;
import ai.chat2db.community.domain.api.service.task.ITaskDataImportService;
import ai.chat2db.community.domain.api.service.task.ITaskExecutionService;
import ai.chat2db.community.domain.api.service.task.ITaskExportService;
import ai.chat2db.community.domain.api.service.task.ITaskFileService;
import ai.chat2db.community.domain.api.service.task.ITaskRecordService;
import ai.chat2db.community.domain.api.service.task.ITaskSchedulerService;
import ai.chat2db.community.domain.core.converter.ConnectionContextConverter;
import ai.chat2db.community.domain.core.impl.task.extension.TaskExtensionManager;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTransferServiceExtensionTest {

    @TempDir
    Path tempDir;

    @Test
    void submissionHookCompletesBeforeTaskIsEnqueued() {
        List<String> events = new ArrayList<>();
        AtomicReference<TaskSubmissionContext> captured = new AtomicReference<>();
        TaskExtensionManager extensionManager = new TaskExtensionManager(List.of(context -> {
            events.add("capture");
            captured.set(context);
        }), List.of());
        TaskTransferServiceImpl service = new TaskTransferServiceImpl(
                executionService(), noOp(ITaskExportService.class), noOp(ITaskDataExportService.class),
                noOp(ITaskDataImportService.class), taskRecordService(), taskFileService(), schedulerService(events),
                new ConnectionContextConverter(), extensionManager);
        TaskOtherFileExportRequest request = new TaskOtherFileExportRequest();
        request.setDatabaseName("shop");
        request.setSchemaName("public");
        request.setTableNames(List.of("orders"));
        request.setExportType("csv");
        request.setContainsHeader(true);
        request.setExportPath(tempDir.toString());
        Chat2DBContext.removeContext();

        Long taskId = service.exportOtherFile(request);

        assertEquals(42L, taskId);
        assertEquals(List.of("capture", "submit"), events);
        assertEquals(42L, captured.get().getTaskId());
        assertEquals(TaskTypeEnum.DOWNLOAD_TABLE_DATA, captured.get().getTaskType());
        assertEquals(TaskOperation.EXPORT, captured.get().getOperation());
        assertEquals("shop", captured.get().getDatabaseName());
        assertEquals(List.of("orders"), captured.get().getTableNames());
    }

    private ITaskExecutionService executionService() {
        return proxy(ITaskExecutionService.class, (proxy, method, args) -> args[args.length - 1]);
    }

    private ITaskRecordService taskRecordService() {
        return proxy(ITaskRecordService.class, (proxy, method, args) ->
                "createTask".equals(method.getName()) ? 42L : null);
    }

    private ITaskFileService taskFileService() {
        return proxy(ITaskFileService.class, (proxy, method, args) -> switch (method.getName()) {
            case "resolveTaskName" -> "orders";
            case "createOtherExportContext" -> new ExportAsyncContext(null, null,
                    tempDir.resolve("orders.csv").toFile(), "csv", List.of("orders"), "single", true);
            default -> null;
        });
    }

    private ITaskSchedulerService schedulerService(List<String> events) {
        return proxy(ITaskSchedulerService.class, (proxy, method, args) -> {
            if ("submit".equals(method.getName())) {
                events.add("submit");
            }
            if (method.getReturnType() == boolean.class) {
                return false;
            }
            return null;
        });
    }

    private static <T> T noOp(Class<T> type) {
        return proxy(type, (proxy, method, args) -> null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(TaskTransferServiceExtensionTest.class.getClassLoader(),
                new Class<?>[]{type}, handler);
    }
}
