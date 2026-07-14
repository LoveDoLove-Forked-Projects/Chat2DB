package ai.chat2db.community.domain.api.model.request.db;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DbDlExecuteRequest {


    @NotBlank
    private String sql;


    @NotNull
    private Long consoleId;


    @NotNull
    private Long dataSourceId;


    private String databaseName;


    private String schemaName;


    private String tableName;


    @Min(1)
    private Integer pageNo;


    @Min(1)
    private Integer pageSize;


    private Boolean pageSizeAll;


    private boolean single;


    private Integer resultSetId;

    private Boolean errorContinue;

    private boolean explain;
}
