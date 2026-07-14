package ai.chat2db.community.domain.api.model.request.sql;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbSqlKeywordRequest {

    @NotNull
    private Long dataSourceId;
    private String databaseName;
    private String schemaName;
    @NotBlank
    private String sql;


}
