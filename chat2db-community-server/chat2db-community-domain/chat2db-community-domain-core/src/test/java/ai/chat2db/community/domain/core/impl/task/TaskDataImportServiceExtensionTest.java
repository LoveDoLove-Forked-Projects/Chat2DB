package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.enums.TaskTypeEnum;
import ai.chat2db.community.domain.api.model.task.extension.TaskExecutionContext;
import ai.chat2db.community.domain.api.model.task.extension.TaskOperation;
import ai.chat2db.community.domain.api.model.task.extension.TaskStatementContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionOperation;
import ai.chat2db.community.domain.api.service.db.extension.ISqlExecutionPolicy;
import ai.chat2db.community.domain.api.service.task.extension.ITaskExecutionGuard;
import ai.chat2db.community.domain.core.impl.db.extension.SqlExecutionPolicyManager;
import ai.chat2db.community.domain.core.impl.task.extension.TaskExtensionManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskDataImportServiceExtensionTest {

    @Test
    void singleStatementGuardRejectsBeforeConnectionLookup() {
        AtomicReference<SqlExecutionContext> planned = new AtomicReference<>();
        AtomicBoolean beforeExecute = new AtomicBoolean();
        Denied denied = new Denied();
        ITaskExecutionGuard guard = guard(context -> {
            assertEquals("delete from orders", context.getSql());
            assertEquals(SqlExecutionOperation.IMPORT, planned.get().getOperation());
            throw denied;
        });
        TaskExtensionManager manager = new TaskExtensionManager(List.of(), List.of(guard));
        ISqlExecutionPolicy policy = new ISqlExecutionPolicy() {
            @Override
            public String rewriteSql(SqlExecutionContext context, String sql) {
                planned.set(context);
                return sql + " where tenant_id = 7";
            }

            @Override
            public void beforeExecute(ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionPlan plan) {
                beforeExecute.set(true);
            }
        };
        TaskDataImportServiceImpl service = new TaskDataImportServiceImpl(manager,
                new SqlExecutionPolicyManager(List.of(policy)));

        manager.runGuarded(taskContext(), () ->
                assertEquals(denied, assertThrows(Denied.class,
                        () -> service.executeSql(0, "delete from orders"))));
        assertEquals(false, beforeExecute.get());
    }

    @Test
    void everyInsertIsGuardedBeforeBatchConnectionLookup() {
        List<String> guarded = new ArrayList<>();
        Denied denied = new Denied();
        ITaskExecutionGuard guard = guard(context -> {
            guarded.add(context.getSql());
            if (guarded.size() == 2) {
                throw denied;
            }
        });
        TaskExtensionManager manager = new TaskExtensionManager(List.of(), List.of(guard));
        TaskDataImportServiceImpl service = new TaskDataImportServiceImpl(manager,
                new SqlExecutionPolicyManager(List.of()));

        manager.runGuarded(taskContext(), () ->
                assertEquals(denied, assertThrows(Denied.class, () -> service.executeBatch(0,
                        List.of("insert into orders values (1)", "insert into orders values (2)")))));

        assertEquals(List.of("insert into orders values (1)", "insert into orders values (2)"), guarded);
    }

    private static ITaskExecutionGuard guard(java.util.function.Consumer<TaskStatementContext> statementGuard) {
        return new ITaskExecutionGuard() {
            @Override
            public void beforeTask(TaskExecutionContext context) {
            }

            @Override
            public void beforeStatement(TaskStatementContext context) {
                statementGuard.accept(context);
            }
        };
    }

    private static TaskExecutionContext taskContext() {
        return new TaskExecutionContext(42L, TaskTypeEnum.UPLOAD_TABLE_DATA, null,
                "shop", null, List.of("orders"), TaskOperation.IMPORT);
    }

    private static final class Denied extends RuntimeException {
    }
}
