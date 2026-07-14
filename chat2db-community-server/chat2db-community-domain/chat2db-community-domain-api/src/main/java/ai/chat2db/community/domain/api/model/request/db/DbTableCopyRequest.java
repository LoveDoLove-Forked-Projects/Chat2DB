package ai.chat2db.community.domain.api.model.request.db;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DbTableCopyRequest {

    @NotNull
    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    @NotBlank
    private String tableName;

    @NotBlank
    private String newName;

    private boolean copyData;
}
