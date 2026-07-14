package ai.chat2db.community.storage.large;

import ai.chat2db.community.domain.api.model.task.Task;

public class TaskStorage extends LargeDataStorage<Task> {
    public static final TaskStorage INSTANCE = new TaskStorage();
    protected TaskStorage() {
        super("task", Task.class, 20);
    }
}
