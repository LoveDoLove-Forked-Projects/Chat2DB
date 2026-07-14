package ai.chat2db.spi.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSqlRequest {

    private String databaseName;

    private String schemaName;

    @NotBlank
    private String tableName;

    @NotEmpty
    private Map<String, String> row;

    private Map<String, String> primaryKeyMap;
}
