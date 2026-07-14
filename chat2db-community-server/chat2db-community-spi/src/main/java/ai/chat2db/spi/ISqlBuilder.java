package ai.chat2db.spi;

import ai.chat2db.spi.sql.builder.IDdlSqlBuilder;
import ai.chat2db.spi.sql.builder.IDmlSqlBuilder;
import ai.chat2db.spi.sql.builder.IDqlSqlBuilder;
import ai.chat2db.spi.sql.builder.IIdentifierSqlBuilder;

/**
 * Unified dialect-specific SQL builder entry.
 */
public interface ISqlBuilder {

    String SQL_BUILDER_SEGMENT_IDENTIFIER = "identifier";
    String SQL_BUILDER_SEGMENT_DQL = "dql";
    String SQL_BUILDER_SEGMENT_DML = "dml";
    String SQL_BUILDER_SEGMENT_DDL = "ddl";
    String ERROR_UNSUPPORTED_SQL_BUILDER_SEGMENT = "SQL builder segment is not supported: ";

    default IIdentifierSqlBuilder identifier() {
        throw unsupportedSqlBuilder(SQL_BUILDER_SEGMENT_IDENTIFIER);
    }

    default IDqlSqlBuilder dql() {
        throw unsupportedSqlBuilder(SQL_BUILDER_SEGMENT_DQL);
    }

    default IDmlSqlBuilder dml() {
        throw unsupportedSqlBuilder(SQL_BUILDER_SEGMENT_DML);
    }

    default IDdlSqlBuilder ddl() {
        throw unsupportedSqlBuilder(SQL_BUILDER_SEGMENT_DDL);
    }

    private static UnsupportedOperationException unsupportedSqlBuilder(String builderName) {
        return new UnsupportedOperationException(ERROR_UNSUPPORTED_SQL_BUILDER_SEGMENT + builderName);
    }
}
