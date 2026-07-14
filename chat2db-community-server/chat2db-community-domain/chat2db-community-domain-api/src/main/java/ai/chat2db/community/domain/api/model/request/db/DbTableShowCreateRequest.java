package ai.chat2db.community.domain.api.model.request.db;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DbTableShowCreateRequest {


    @NotNull
    private Long dataSourceId;


    @NotNull
    private String databaseName;


    private String tableName;


    private String schemaName;
}
