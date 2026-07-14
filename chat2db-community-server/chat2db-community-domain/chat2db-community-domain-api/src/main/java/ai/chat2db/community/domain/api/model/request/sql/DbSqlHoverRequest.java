package ai.chat2db.community.domain.api.model.request.sql;

import ai.chat2db.community.domain.api.model.db.SimpleIdentifier;
import ai.chat2db.community.domain.api.model.sql.SqlStatement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbSqlHoverRequest {
    @NotNull
    private Long dataSourceId;


    @Valid
    @NotNull
    private SqlStatement currentStatement;


    @Valid
    @NotNull
    private SimpleIdentifier hoverIdentifier;


    private String databaseName;


    private String schemaName;

}
