package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import ai.chat2db.community.web.api.model.request.data.source.IDataSourceConsoleRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DmlTableRequest extends DataSourceBaseRequest implements IDataSourceConsoleRequestInfo {


    @NotNull
    private String tableName;


    @NotNull
    private Long consoleId;


    private Integer pageNo;


    private Integer pageSize;


    private Boolean pageSizeAll;


    private String sql;
}
