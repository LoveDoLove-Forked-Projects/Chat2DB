package ai.chat2db.community.domain.api.model.request.pin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbTablePinRequest {

    @NotNull
    private Long dataSourceId;


    private String databaseName;


    private String schemaName;


    private String tableName;


    private Long userId;
}
