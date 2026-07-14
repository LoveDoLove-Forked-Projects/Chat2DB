package ai.chat2db.community.web.api.model.request.task;

import ai.chat2db.community.domain.api.enums.ExportScopeTypeEnum;
import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.Data;

import java.util.List;

@Data
public class SqlFileExportRequest extends DataSourceBaseRequest {


    private boolean containData;


    private List<String> tableNames;


    private String exportPath;


    private ExportScopeTypeEnum scope;
}
