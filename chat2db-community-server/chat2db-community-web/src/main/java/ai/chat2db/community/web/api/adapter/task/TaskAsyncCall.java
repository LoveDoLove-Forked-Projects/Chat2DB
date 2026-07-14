package ai.chat2db.community.web.api.adapter.task;

import ai.chat2db.community.domain.api.model.request.task.TaskRecordUpdateRequest;
import ai.chat2db.community.web.api.storage.WorkspaceStorageWebFacade;
import ai.chat2db.community.domain.api.service.task.ITaskAsyncCall;

import java.util.Map;

public class TaskAsyncCall implements ITaskAsyncCall {

    private Long taskId;

    public TaskAsyncCall(Long taskId) {
        this.taskId = taskId;
    }

    @Override
    public void update(Map<String, Object> map) {
        TaskRecordUpdateRequest taskUpdateParam = new TaskRecordUpdateRequest();
        taskUpdateParam.setId(taskId);
        Object progress = map.get("progress");
        if (progress != null) {
            taskUpdateParam.setTaskProgress(String.valueOf(progress));
        }
        Object info = map.get("info");
        if (info != null) {
            taskUpdateParam.setInfoLog(String.valueOf(info));
        }
        Object error = map.get("error");
        if (error != null) {
            taskUpdateParam.setErrorLog(String.valueOf(error));
        }
        Object status = map.get("status");
        if (status != null) {
            taskUpdateParam.setTaskStatus(String.valueOf(status));
        }
        Object downloadUrl = map.get("downloadUrl");
        if (downloadUrl != null) {
            taskUpdateParam.setDownloadUrl(String.valueOf(downloadUrl));
        }
        WorkspaceStorageWebFacade.updateTask(taskUpdateParam);
    }

}
