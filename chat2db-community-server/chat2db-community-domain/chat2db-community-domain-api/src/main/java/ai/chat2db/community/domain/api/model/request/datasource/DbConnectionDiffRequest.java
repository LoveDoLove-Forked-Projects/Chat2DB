package ai.chat2db.community.domain.api.model.request.datasource;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbConnectionDiffRequest {

    @NotNull
    private Long dataSourceId;

    private String databaseName;

    private String schemaName;
}
