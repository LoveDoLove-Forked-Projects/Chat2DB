package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.IDataSourceBaseRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DatabaseDeletePrepareRequest implements IDataSourceBaseRequestInfo {

    @NotNull
    private Long dataSourceId;

    private String databaseName;
}
