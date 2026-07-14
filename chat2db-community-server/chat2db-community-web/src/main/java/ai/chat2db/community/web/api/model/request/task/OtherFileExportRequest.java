package ai.chat2db.community.web.api.model.request.task;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OtherFileExportRequest extends DataSourceBaseRequest {

    @NotNull
    private String exportType;


    @NotEmpty
    private List<String> tableNames;


    private Boolean containsHeader;


    private String exportPath;
}
