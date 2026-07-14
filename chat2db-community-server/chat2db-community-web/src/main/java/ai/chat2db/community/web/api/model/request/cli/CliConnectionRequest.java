package ai.chat2db.community.web.api.model.request.cli;

import ai.chat2db.community.web.api.model.request.data.source.IDataSourceBaseRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CliConnectionRequest extends CliPageRequest implements IDataSourceBaseRequestInfo {

    @NotNull
    private Long dataSourceId;

    private String databaseName;

    private String schemaName;
}
