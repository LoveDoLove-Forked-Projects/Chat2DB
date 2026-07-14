package ai.chat2db.plugin.presto;

import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.DefaultMetaService;

public class PrestoMetaData extends DefaultMetaService implements IDbMetaData {
    public String tableDDL(String databaseName, String schemaName,String tableName) {
        return "";
    }
}
