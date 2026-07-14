package ai.chat2db.community.web.api.model.request.db;
import jakarta.validation.constraints.NotNull;
import ai.chat2db.community.tools.wrapper.request.PageQueryRequest;
import ai.chat2db.community.web.api.model.request.data.source.IDataSourceBaseRequestInfo;
import lombok.Data;
@Data
public class TableBriefQueryRequest extends PageQueryRequest implements IDataSourceBaseRequestInfo {
    @NotNull
    private Long dataSourceId;
    private String databaseName;
    private String schemaName;
    private String searchKey;
    private boolean refresh;
}
