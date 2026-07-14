package ai.chat2db.plugin.postgresql.identifier;

import ai.chat2db.spi.DefaultSQLIdentifierProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class PostgreSQLIdentifierProcessor extends DefaultSQLIdentifierProcessor {
    private static final Set<String> PGSQL_RESERVED_KEYWORDS = new HashSet<>();

    static {
        PGSQL_RESERVED_KEYWORDS.add("ALL");
        PGSQL_RESERVED_KEYWORDS.add("ANALYSE");
        PGSQL_RESERVED_KEYWORDS.add("ANALYZE");
        PGSQL_RESERVED_KEYWORDS.add("AND");
        PGSQL_RESERVED_KEYWORDS.add("ANY");
        PGSQL_RESERVED_KEYWORDS.add("ARRAY");
        PGSQL_RESERVED_KEYWORDS.add("AS");
        PGSQL_RESERVED_KEYWORDS.add("ASC");
        PGSQL_RESERVED_KEYWORDS.add("ASYMMETRIC");
        PGSQL_RESERVED_KEYWORDS.add("BOTH");
        PGSQL_RESERVED_KEYWORDS.add("CASE");
        PGSQL_RESERVED_KEYWORDS.add("CAST");
        PGSQL_RESERVED_KEYWORDS.add("CHECK");
        PGSQL_RESERVED_KEYWORDS.add("COLLATE");
        PGSQL_RESERVED_KEYWORDS.add("COLUMN");
        PGSQL_RESERVED_KEYWORDS.add("CONSTRAINT");
        PGSQL_RESERVED_KEYWORDS.add("CREATE");
        PGSQL_RESERVED_KEYWORDS.add("CURRENT_CATALOG");
        PGSQL_RESERVED_KEYWORDS.add("CURRENT_DATE");
        PGSQL_RESERVED_KEYWORDS.add("CURRENT_ROLE");
        PGSQL_RESERVED_KEYWORDS.add("CURRENT_TIME");
        PGSQL_RESERVED_KEYWORDS.add("CURRENT_TIMESTAMP");
        PGSQL_RESERVED_KEYWORDS.add("CURRENT_USER");
        PGSQL_RESERVED_KEYWORDS.add("DEFAULT");
        PGSQL_RESERVED_KEYWORDS.add("DEFERRABLE");
        PGSQL_RESERVED_KEYWORDS.add("DESC");
        PGSQL_RESERVED_KEYWORDS.add("DISTINCT");
        PGSQL_RESERVED_KEYWORDS.add("DO");
        PGSQL_RESERVED_KEYWORDS.add("ELSE");
        PGSQL_RESERVED_KEYWORDS.add("END");
        PGSQL_RESERVED_KEYWORDS.add("EXCEPT");
        PGSQL_RESERVED_KEYWORDS.add("FALSE");
        PGSQL_RESERVED_KEYWORDS.add("FETCH");
        PGSQL_RESERVED_KEYWORDS.add("FOR");
        PGSQL_RESERVED_KEYWORDS.add("FOREIGN");
        PGSQL_RESERVED_KEYWORDS.add("FROM");
        PGSQL_RESERVED_KEYWORDS.add("GRANT");
        PGSQL_RESERVED_KEYWORDS.add("GROUP");
        PGSQL_RESERVED_KEYWORDS.add("HAVING");
        PGSQL_RESERVED_KEYWORDS.add("IN");
        PGSQL_RESERVED_KEYWORDS.add("INITIALLY");
        PGSQL_RESERVED_KEYWORDS.add("INTERSECT");
        PGSQL_RESERVED_KEYWORDS.add("INTO");
        PGSQL_RESERVED_KEYWORDS.add("LATERAL");
        PGSQL_RESERVED_KEYWORDS.add("LEADING");
        PGSQL_RESERVED_KEYWORDS.add("LIMIT");
        PGSQL_RESERVED_KEYWORDS.add("LOCALTIME");
        PGSQL_RESERVED_KEYWORDS.add("LOCALTIMESTAMP");
        PGSQL_RESERVED_KEYWORDS.add("NOT");
        PGSQL_RESERVED_KEYWORDS.add("NULL");
        PGSQL_RESERVED_KEYWORDS.add("OFFSET");
        PGSQL_RESERVED_KEYWORDS.add("ON");
        PGSQL_RESERVED_KEYWORDS.add("ONLY");
        PGSQL_RESERVED_KEYWORDS.add("OR");
        PGSQL_RESERVED_KEYWORDS.add("ORDER");
        PGSQL_RESERVED_KEYWORDS.add("PLACING");
        PGSQL_RESERVED_KEYWORDS.add("PRIMARY");
        PGSQL_RESERVED_KEYWORDS.add("REFERENCES");
        PGSQL_RESERVED_KEYWORDS.add("RETURNING");
        PGSQL_RESERVED_KEYWORDS.add("SELECT");
        PGSQL_RESERVED_KEYWORDS.add("SESSION_USER");
        PGSQL_RESERVED_KEYWORDS.add("SOME");
        PGSQL_RESERVED_KEYWORDS.add("SYMMETRIC");
        PGSQL_RESERVED_KEYWORDS.add("TABLE");
        PGSQL_RESERVED_KEYWORDS.add("THEN");
        PGSQL_RESERVED_KEYWORDS.add("TO");
        PGSQL_RESERVED_KEYWORDS.add("TRAILING");
        PGSQL_RESERVED_KEYWORDS.add("TRUE");
        PGSQL_RESERVED_KEYWORDS.add("UNION");
        PGSQL_RESERVED_KEYWORDS.add("UNIQUE");
        PGSQL_RESERVED_KEYWORDS.add("USER");
        PGSQL_RESERVED_KEYWORDS.add("USING");
        PGSQL_RESERVED_KEYWORDS.add("VARIADIC");
        PGSQL_RESERVED_KEYWORDS.add("WHEN");
        PGSQL_RESERVED_KEYWORDS.add("WHERE");
        PGSQL_RESERVED_KEYWORDS.add("WINDOW");
        PGSQL_RESERVED_KEYWORDS.add("WITH");
    }


    @Override
    public boolean isReservedKeyword(String identifier, Integer majorVersion, Integer minorVersion) {
        return PGSQL_RESERVED_KEYWORDS.contains(identifier);
    }

    @Override
    public String quoteIdentifier(String identifier, Integer majorVersion, Integer minorVersion) {
        if (isValidIdentifier(identifier)) {
            if (containsUpperCase(identifier) || isReservedKeyword(identifier.toUpperCase(), majorVersion, minorVersion)) {
                return StringUtils.wrap(identifier, '"');
            }
            return identifier;
        }
        return StringUtils.wrap(identifier, '"');

    }


    @Override
    public String quoteIdentifier(String identifier) {
        if (isQuoteIdentifier(identifier)) {
            return identifier;
        }
        if (isValidIdentifier(identifier)) {
            if (containsUpperCase(identifier) || isReservedKeyword(identifier.toUpperCase(), null, null)) {
                return StringUtils.wrap(identifier, '"');
            }
            return identifier;
        }
        return StringUtils.wrap(identifier, '"');
    }

    @Override
    public String quoteIdentifierIgnoreCase(String identifier) {
        if (isValidIdentifier(identifier)) {
            if (isReservedKeyword(identifier.toUpperCase(), null, null)) {
                return StringUtils.wrap(identifier, '"');
            }
            return identifier;
        }
        return StringUtils.wrap(identifier, '"');
    }

    @Override
    public String convertIdentifierCase(String identifier) {
        if (StringUtils.isBlank(identifier)) {
            return identifier;
        } else {
            return identifier.toLowerCase();
        }
    }
}
