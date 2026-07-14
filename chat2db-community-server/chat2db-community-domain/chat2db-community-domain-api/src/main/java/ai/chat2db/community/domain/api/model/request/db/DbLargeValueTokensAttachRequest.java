package ai.chat2db.community.domain.api.model.request.db;

import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.ResultCell;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DbLargeValueTokensAttachRequest {

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    private String tableName;

    @NotEmpty
    private List<Header> headers;

    @NotEmpty
    private List<List<ResultCell>> dataList;

    private boolean canEdit;
}
