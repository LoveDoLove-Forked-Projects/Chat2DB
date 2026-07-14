package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DataExportRequest extends DataSourceBaseRequest {


    private String sql;


    private String originalSql;


    @NotNull
    private String exportType;


    @NotNull
    private String exportSize;


    private Integer resultSetId;
}
