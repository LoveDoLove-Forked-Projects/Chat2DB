package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.IDataSourceBaseRequestInfo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TableGenerateClassRequest implements IDataSourceBaseRequestInfo {


    @NotEmpty
    private String tableName;


    @NotNull
    private Long dataSourceId;


    private String databaseName;


    private String schemaName;
    private String exportPath;

}
