package ai.chat2db.community.domain.api.model.request.db;
import ai.chat2db.community.tools.wrapper.param.QueryParam;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DbTableQueryRequest extends QueryParam {
    @NotNull
    private Long dataSourceId;
    @NotNull
    private String databaseName;
    private String tableName;
    private String tableType;
    private String schemaName;
    private boolean refresh;
}
