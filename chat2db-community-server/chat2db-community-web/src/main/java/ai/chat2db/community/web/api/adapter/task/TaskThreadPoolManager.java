package ai.chat2db.community.web.api.adapter.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskThreadPoolManager {

    private static final Map<Long, TaskThread> taskMap = new ConcurrentHashMap<>();

    public static void submitTask(Long taskId, TaskThread task) {
        taskMap.put(taskId, task);
        task.start();
    }

    public static void cancelTask(Long taskId) {
        TaskThread thread = taskMap.get(taskId);
        if (thread != null) {
            thread.cancel();
            thread.stop();
        }
    }

    public static void remove(Long taskId) {
        taskMap.remove(taskId);
    }
}
