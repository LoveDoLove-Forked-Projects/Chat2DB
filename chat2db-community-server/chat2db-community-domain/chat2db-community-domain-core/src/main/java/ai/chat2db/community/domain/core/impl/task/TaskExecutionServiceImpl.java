package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.model.runtime.ConnectionProfile;
import ai.chat2db.community.domain.api.model.task.extension.TaskExecutionContext;
import ai.chat2db.community.domain.api.service.task.ITaskExecutionService;
import ai.chat2db.community.domain.core.converter.ConnectionContextConverter;
import ai.chat2db.community.domain.core.impl.task.extension.TaskExtensionManager;
import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.util.ContextUtils;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

@Service
public class TaskExecutionServiceImpl implements ITaskExecutionService {

    private final ConnectionContextConverter connectionContextConverter;
    private final TaskExtensionManager taskExtensionManager;

    public TaskExecutionServiceImpl(ConnectionContextConverter connectionContextConverter,
            TaskExtensionManager taskExtensionManager) {
        this.connectionContextConverter = connectionContextConverter;
        this.taskExtensionManager = taskExtensionManager;
    }

    @Override
    public Runnable withCurrentConnectionContext(Context context, Runnable runnable) {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        ConnectInfo snapshot = connectInfo == null ? null : connectInfo.copy();
        return withConnectionSnapshot(context, snapshot, null, runnable);
    }

    @Override
    public Runnable withCurrentConnectionContext(Context context, TaskExecutionContext taskContext,
            Runnable runnable) {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        ConnectInfo snapshot = connectInfo == null ? null : connectInfo.copy();
        return withConnectionSnapshot(context, snapshot, taskContext, runnable);
    }

    @Override
    public Runnable withConnectionProfile(Context context, ConnectionProfile profile, Runnable runnable) {
        ConnectInfo snapshot = connectionContextConverter.profile2connectInfo(profile);
        return withConnectionSnapshot(context, snapshot, null, runnable);
    }

    @Override
    public Runnable withConnectionProfile(Context context, ConnectionProfile profile,
            TaskExecutionContext taskContext, Runnable runnable) {
        ConnectInfo snapshot = connectionContextConverter.profile2connectInfo(profile);
        return withConnectionSnapshot(context, snapshot, taskContext, runnable);
    }

    private Runnable withConnectionSnapshot(Context context, ConnectInfo connectInfo,
            TaskExecutionContext taskContext, Runnable runnable) {
        ConnectInfo snapshot = connectInfo == null ? null : connectInfo.copy();
        return () -> {
            Context previousContext = ContextUtils.queryContext();
            ConnectInfo previousConnectInfo = Chat2DBContext.getConnectInfo();
            try {
                ContextUtils.setContext(context);
                if (snapshot != null) {
                    Chat2DBContext.putContext(snapshot.copy());
                }
                if (taskContext == null) {
                    runnable.run();
                } else {
                    taskExtensionManager.runGuarded(taskContext, runnable);
                }
            } finally {
                restoreContext(previousContext);
                restoreConnectionContext(previousConnectInfo);
            }
        };
    }

    private static void restoreContext(Context previousContext) {
        if (previousContext == null) {
            ContextUtils.removeContext();
        } else {
            ContextUtils.setContext(previousContext);
        }
    }

    private static void restoreConnectionContext(ConnectInfo previousConnectInfo) {
        if (previousConnectInfo == null) {
            Chat2DBContext.removeContext();
        } else {
            Chat2DBContext.putContext(previousConnectInfo);
        }
    }
}
