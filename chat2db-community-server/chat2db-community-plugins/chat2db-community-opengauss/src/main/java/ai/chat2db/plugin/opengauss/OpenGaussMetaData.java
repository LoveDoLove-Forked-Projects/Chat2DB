package ai.chat2db.plugin.opengauss;

import ai.chat2db.plugin.postgresql.PostgreSQLMetaData;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static ai.chat2db.plugin.opengauss.constant.OpenGaussMetaDataConstants.SYSTEM_DATABASES;
import static ai.chat2db.plugin.opengauss.constant.OpenGaussMetaDataConstants.SYSTEM_SCHEMAS;

@Slf4j
public class OpenGaussMetaData extends PostgreSQLMetaData implements IDbMetaData {

    @Override
    public List<Database> databases(Connection connection) {
        log.info("OpenGaussMetaData databases");
        List<Database> databases = super.databases(connection);
        if(CollectionUtils.isEmpty(databases)){
            log.info("OpenGaussMetaData_1");
            return databases;
        }
        List<Database> result = new ArrayList<>();
        for (Database database : databases) {
            if(SYSTEM_DATABASES.contains(database.getName())){
                continue;
            }
            result.add(database);
        }
        return result;
    }

    @Override
    public List<Schema> schemas(Connection connection, String database) {
        List<Schema> schemas = super.schemas(connection,database);
        if(CollectionUtils.isEmpty(schemas)){
            return schemas;
        }
        List<Schema> result = new ArrayList<>();
        for (Schema schema : schemas) {
            if(SYSTEM_SCHEMAS.contains(schema.getName())){
                continue;
            }
            result.add(schema);
        }
        return result;
    }
}
