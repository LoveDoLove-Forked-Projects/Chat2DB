package ai.chat2db.community.domain.api.model.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSqlFileExportRequest {

    private String databaseName;

    private String schemaName;

    @NotEmpty
    private List<String> tableNames;

    @NotBlank
    private String scope;

    private String exportPath;

    private boolean containData;
}
