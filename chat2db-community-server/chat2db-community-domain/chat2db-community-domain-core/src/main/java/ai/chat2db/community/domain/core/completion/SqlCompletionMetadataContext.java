package ai.chat2db.community.domain.core.completion;

import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.ISQLIdentifierProcessor;
import ai.chat2db.community.domain.api.config.DBConfig;
import java.sql.Connection;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class SqlCompletionMetadataContext {

    Long dataSourceId;
    String databaseName;
    String schemaName;
    String datasourceName;
    DBConfig dbConfig;
    IDbMetaData metaData;
    Supplier<Connection> connectionSupplier;
    ISQLIdentifierProcessor identifierProcessor;
}
