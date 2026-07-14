package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.enums.ExportTypeEnum;
import ai.chat2db.community.domain.api.model.request.db.DbDmlExportRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DbDmlExportPlan {

    private String fileName;

    private ExportTypeEnum exportType;

    private DbDmlExportRequest exportRequest;
}
