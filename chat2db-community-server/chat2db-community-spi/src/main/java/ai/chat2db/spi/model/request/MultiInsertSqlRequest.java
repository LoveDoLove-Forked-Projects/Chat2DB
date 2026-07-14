package ai.chat2db.spi.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiInsertSqlRequest {

    private String databaseName;

    private String schemaName;

    @NotBlank
    private String tableName;

    @NotEmpty
    private List<String> columnList;

    @NotEmpty
    private List<List<String>> valueLists;
}
