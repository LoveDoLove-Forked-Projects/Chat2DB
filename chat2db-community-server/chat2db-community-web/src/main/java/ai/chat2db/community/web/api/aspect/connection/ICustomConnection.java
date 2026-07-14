package ai.chat2db.community.web.api.aspect.connection;

import ai.chat2db.community.domain.api.model.request.runtime.DbConnectionContextRequest;

public interface ICustomConnection {


    DbConnectionContextRequest getConnectionInfo(Long datasourceId,String databaseName,String schemaName,Long consolerId);
}
