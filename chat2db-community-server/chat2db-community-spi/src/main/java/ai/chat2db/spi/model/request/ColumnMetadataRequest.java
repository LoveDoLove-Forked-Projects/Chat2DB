package ai.chat2db.spi.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMetadataRequest {

    private String databaseName;

    private String schemaName;

    @NotEmpty
    private String tableName;

    private String columnName;
}
