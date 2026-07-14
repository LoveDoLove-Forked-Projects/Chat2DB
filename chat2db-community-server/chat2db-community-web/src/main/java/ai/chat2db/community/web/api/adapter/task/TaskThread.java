package ai.chat2db.community.web.api.adapter.task;

import ai.chat2db.community.domain.api.model.async.AsyncContext;
import ai.chat2db.community.tools.model.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskThread extends Thread {

    private Context context;

    private AsyncContext asyncContext;

    private Long taskId;

    private Runnable runnable;

    public TaskThread(Context context, AsyncContext asyncContext, Long taskId, Runnable runnable) {
        super(runnable);
        this.context = context;
        this.asyncContext = asyncContext;
        this.taskId = taskId;
        this.runnable = runnable;
    }

    public void cancel() {
        asyncContext.stop();
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception e){
            log.error("task error", e);
            asyncContext.error(e.getMessage());
        }finally {
            TaskThreadPoolManager.remove(taskId);
            asyncContext.finish();
        }
    }
}
