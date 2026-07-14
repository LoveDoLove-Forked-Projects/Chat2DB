package ai.chat2db.community.web.api.model.request.db;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import ai.chat2db.community.web.api.model.request.data.source.IDataSourceConsoleRequestInfo;

import lombok.Data;


@Data
public class DdlCountRequest extends DataSourceBaseRequest implements IDataSourceConsoleRequestInfo {


    @NotNull
    private String sql;


    @NotNull
    private Long consoleId;


    @NotNull
    private String tableName;
}
