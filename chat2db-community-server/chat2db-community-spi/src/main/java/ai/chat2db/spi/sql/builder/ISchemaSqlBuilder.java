package ai.chat2db.spi.sql.builder;

import ai.chat2db.community.domain.api.model.metadata.Schema;

public interface ISchemaSqlBuilder {

    String buildCreateSchema(Schema schema);

    String buildAlterSchema(String oldSchemaName, String newSchemaName);

    String buildDropSchema(String schemaName);
}
