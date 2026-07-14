package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.model.runtime.ConnectionProfile;
import ai.chat2db.community.domain.api.service.task.ITaskExecutionService;
import ai.chat2db.community.domain.core.converter.ConnectionContextConverter;
import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.util.ContextUtils;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskExecutionServiceImpl implements ITaskExecutionService {

    @Autowired
    private ConnectionContextConverter connectionContextConverter;

    @Override
    public Runnable withCurrentConnectionContext(Context context, Runnable runnable) {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        ConnectInfo snapshot = connectInfo == null ? null : connectInfo.copy();
        return () -> {
            try {
                ContextUtils.setContext(context);
                if (snapshot != null) {
                    Chat2DBContext.putContext(snapshot);
                }
                runnable.run();
            } finally {
                ContextUtils.removeContext();
                Chat2DBContext.removeContext();
            }
        };
    }

    @Override
    public Runnable withConnectionProfile(Context context, ConnectionProfile profile, Runnable runnable) {
        ConnectInfo snapshot = connectionContextConverter.profile2connectInfo(profile);
        return () -> {
            try {
                ContextUtils.setContext(context);
                if (snapshot != null) {
                    Chat2DBContext.putContext(snapshot.copy());
                }
                runnable.run();
            } finally {
                ContextUtils.removeContext();
                Chat2DBContext.removeContext();
            }
        };
    }
}
