package ai.chat2db.spi.sql.builder;

import ai.chat2db.community.domain.api.model.metadata.Database;

public interface IDatabaseSqlBuilder {

    String buildCreateDatabase(Database database);

    String buildAlterDatabase(Database oldDatabase, Database newDatabase);

    String buildDropDatabase(String databaseName);

    String buildUseDatabase(String databaseName);
}
