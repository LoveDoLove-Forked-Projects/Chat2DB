package ai.chat2db.spi.sql.builder;

public interface IIdentifierSqlBuilder {

    String quoteIdentifier(String identifier);

    String quoteQualifiedIdentifier(String... identifiers);

    String quoteAlias(String alias);
}
