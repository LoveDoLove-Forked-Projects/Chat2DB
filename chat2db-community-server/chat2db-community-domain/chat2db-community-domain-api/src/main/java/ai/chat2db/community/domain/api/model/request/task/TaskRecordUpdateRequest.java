package ai.chat2db.community.domain.api.model.request.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRecordUpdateRequest {


    @NotNull
    private Long id;


    private Long userId;


    private String taskStatus;


    private String taskProgress;


    private String taskName;


    private String downloadUrl;


    private byte[] content;


    private String infoLog;


    private String errorLog;


    private String currentProgress;
}
