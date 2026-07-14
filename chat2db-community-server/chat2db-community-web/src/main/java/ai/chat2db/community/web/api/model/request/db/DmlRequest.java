package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import ai.chat2db.community.web.api.model.request.data.source.IDataSourceConsoleRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DmlRequest extends DataSourceBaseRequest implements IDataSourceConsoleRequestInfo {


    @NotNull
    private String sql;


    private Long consoleId;


    private Integer pageNo;


    private Integer pageSize;


    private Boolean pageSizeAll;


    private Long applyId;


    @NotNull
    private String tableName;


    private boolean single;

    private Integer resultSetId;

    private Boolean errorContinue;

    private boolean explain;
}
