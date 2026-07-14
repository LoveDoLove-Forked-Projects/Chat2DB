package ai.chat2db.spi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TablesRequest {

    @NotNull
    private String databaseName;

    private String schemaName;

    private String tableName;
}
