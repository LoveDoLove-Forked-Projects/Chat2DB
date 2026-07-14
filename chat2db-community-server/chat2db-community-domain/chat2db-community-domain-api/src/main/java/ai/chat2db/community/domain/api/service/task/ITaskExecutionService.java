package ai.chat2db.community.domain.api.service.task;

import ai.chat2db.community.domain.api.model.runtime.ConnectionProfile;
import ai.chat2db.community.tools.model.Context;

public interface ITaskExecutionService {

    /**
     * Wraps a runnable with the current connection context.
     *
     * @param context task context to propagate.
     * @param runnable runnable to execute.
     * @return wrapped runnable.
     */
    Runnable withCurrentConnectionContext(Context context, Runnable runnable);

    /**
     * Wraps a runnable with an explicit connection profile.
     *
     * @param context task context to propagate.
     * @param profile connection profile to bind during execution.
     * @param runnable runnable to execute.
     * @return wrapped runnable.
     */
    Runnable withConnectionProfile(Context context, ConnectionProfile profile, Runnable runnable);
}
