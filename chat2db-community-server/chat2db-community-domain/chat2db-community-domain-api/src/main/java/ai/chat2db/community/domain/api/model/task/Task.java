package ai.chat2db.community.domain.api.model.task;

import lombok.Data;
import java.util.Date;

@Data
public class Task {


    private Long id;


    private Date gmtCreate;


    private Date gmtModified;


    private Long dataSourceId;


    private String databaseName;


    private String schemaName;


    private String tableName;


    private String deleted;


    private Long userId;


    private Long organizationId;


    private String taskType;


    private String taskStatus;


    private String taskProgress;


    private String progress;


    private String progressDesc;


    private String taskName;


    private String downloadUrl;


    private byte[] content;


    private String infoLog;


    private String errorLog;


    private String currentProgress;
}
