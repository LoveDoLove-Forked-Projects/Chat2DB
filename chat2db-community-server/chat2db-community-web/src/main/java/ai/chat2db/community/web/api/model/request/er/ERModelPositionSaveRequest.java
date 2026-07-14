package ai.chat2db.community.web.api.model.request.er;

import ai.chat2db.community.web.api.model.request.data.source.IDataSourceBaseRequestInfo;
import lombok.Data;

@Data
public class ERModelPositionSaveRequest implements IDataSourceBaseRequestInfo {


        private Long dataSourceId;


        private String databaseName;


        private String schemaName;


        private String position;
}
