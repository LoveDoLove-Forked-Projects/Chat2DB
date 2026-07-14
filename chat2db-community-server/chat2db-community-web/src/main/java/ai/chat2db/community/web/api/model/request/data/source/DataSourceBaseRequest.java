package ai.chat2db.community.web.api.model.request.data.source;

import jakarta.validation.constraints.NotNull;

import lombok.Data;


@Data
public class DataSourceBaseRequest implements IDataSourceBaseRequestInfo{


    @NotNull
    private Long dataSourceId;


    private String databaseName;


    private String schemaName;


    private boolean refresh;
}
