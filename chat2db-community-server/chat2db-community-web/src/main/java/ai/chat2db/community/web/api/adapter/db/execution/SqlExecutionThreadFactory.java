package ai.chat2db.community.web.api.adapter.db.execution;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class SqlExecutionThreadFactory implements ThreadFactory {

    private final String prefix;
    private final AtomicInteger index = new AtomicInteger();

    SqlExecutionThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(prefix + "-" + index.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    }
}
