package ai.chat2db.community.domain.api.model.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRecordCreateRequest {


    @NotNull
    private Long dataSourceId;


    private String databaseName;


    private String schemaName;


    private String tableName;


    private Long userId;


    private String taskProgress;


    @NotBlank
    private String taskName;


    @NotBlank
    private String taskType;


}
