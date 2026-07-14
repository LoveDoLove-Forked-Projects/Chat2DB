package ai.chat2db.community.domain.api.model.request.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskOtherFileExportRequest {

    private String databaseName;

    private String schemaName;

    private List<String> tableNames;

    private String exportType;

    private Boolean containsHeader;

    private String exportPath;
}
