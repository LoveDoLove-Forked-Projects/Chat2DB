package ai.chat2db.community.domain.api.service.task;

import ai.chat2db.community.domain.api.model.PageResponse;
import ai.chat2db.community.domain.api.model.request.task.TaskRecordCreateRequest;
import ai.chat2db.community.domain.api.model.request.task.TaskRecordPageRequest;
import ai.chat2db.community.domain.api.model.request.task.TaskRecordUpdateRequest;
import ai.chat2db.community.domain.api.model.task.Task;
import ai.chat2db.community.domain.api.model.task.TaskDownload;

/**
 * Manages persisted async task records.
 */
public interface ITaskRecordService {

    String STATUS_STOP = "STOP";

    PageResponse<Task> taskList(TaskRecordPageRequest request);

    Task getTask(Long id);

    Long createTask(TaskRecordCreateRequest request);

    void updateTask(TaskRecordUpdateRequest request);

    void stopTask(Long id);

    TaskDownload resolveDownload(Long id, Long userId);
}
