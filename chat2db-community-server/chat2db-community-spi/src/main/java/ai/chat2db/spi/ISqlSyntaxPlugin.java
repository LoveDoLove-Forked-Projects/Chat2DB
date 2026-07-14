package ai.chat2db.spi;

import ai.chat2db.spi.ISQLParser;
import ai.chat2db.spi.ISqlCompletionProvider;

/**
 * Provides SQL parser and completion capabilities for one database dialect.
 */
public interface ISqlSyntaxPlugin {

    /**
     * Returns the database type handled by this syntax plugin.
     *
     * @return database type value for the dialect.
     */
    String getDatabaseType();


    /**
     * Returns the SQL parser for this dialect.
     *
     * @return dialect-specific SQL parser.
     */
    ISQLParser getSQLParser();

    /**
     * Returns the SQL completion provider for this dialect.
     *
     * @return completion provider when supported; otherwise an unsupported provider.
     */
    default ISqlCompletionProvider getSqlCompletionProvider() {
        return ISqlCompletionProvider.unsupported(getDatabaseType());
    }
}
