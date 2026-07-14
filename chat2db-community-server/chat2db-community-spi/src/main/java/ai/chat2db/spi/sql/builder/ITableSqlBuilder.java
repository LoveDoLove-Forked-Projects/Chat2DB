package ai.chat2db.spi.sql.builder;

import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.spi.model.request.DropTableRequest;
import ai.chat2db.spi.model.request.TruncateTableRequest;

public interface ITableSqlBuilder {

    String buildCreateTable(Table table, TableBuilderConfig config);

    String buildAlterTable(Table oldTable, Table newTable);

    String buildDropTable(DropTableRequest dropTableRequest);

    String buildTruncateTable(TruncateTableRequest truncateTableRequest);

    String buildAITableSchema(Table table);
}
