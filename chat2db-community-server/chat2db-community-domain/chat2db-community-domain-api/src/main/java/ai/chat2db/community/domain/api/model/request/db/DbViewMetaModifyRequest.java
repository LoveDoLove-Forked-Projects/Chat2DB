package ai.chat2db.community.domain.api.model.request.db;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbViewMetaModifyRequest {


    @NotNull
    private Long dataSourceId;


    private String databaseName;


    private String schemaName;


    private String viewName;


}
