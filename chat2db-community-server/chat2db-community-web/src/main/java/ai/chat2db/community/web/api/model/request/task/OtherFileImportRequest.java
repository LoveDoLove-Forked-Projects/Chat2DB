package ai.chat2db.community.web.api.model.request.task;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class OtherFileImportRequest extends DataSourceBaseRequest {

    @NotNull
    private String importType;

   private String fileName;

   private String tableName;

}
