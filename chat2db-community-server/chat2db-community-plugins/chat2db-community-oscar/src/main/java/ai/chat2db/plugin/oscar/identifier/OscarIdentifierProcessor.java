package ai.chat2db.plugin.oscar.identifier;

import ai.chat2db.spi.DefaultSQLIdentifierProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class OscarIdentifierProcessor extends DefaultSQLIdentifierProcessor {

    private static final Set<String> RESERVED_KEYWORDS = Set.of(
            "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "BEGIN", "BETWEEN", "BY", "CHAR", "CHECK",
            "COLUMN", "COMMENT", "CONNECT", "CONSTRAINT", "CREATE", "CURRENT", "DATE", "DECIMAL", "DEFAULT",
            "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "END", "EXISTS", "FLOAT", "FOR", "FROM", "FUNCTION",
            "GRANT", "GROUP", "HAVING", "IN", "INDEX", "INSERT", "INT", "INTEGER", "INTERSECT", "INTO", "IS",
            "LIKE", "MINUS", "NOT", "NULL", "NUMBER", "ON", "OR", "ORDER", "PRIMARY", "PROCEDURE", "PUBLIC",
            "RETURN", "REVOKE", "ROWNUM", "SELECT", "SET", "SMALLINT", "SYSDATE", "TABLE", "THEN", "TO",
            "TRIGGER", "UNION", "UNIQUE", "UPDATE", "USER", "VALUES", "VARCHAR", "VIEW", "WHERE", "WITH"
    );

    @Override
    public boolean isReservedKeyword(String identifier, Integer majorVersion, Integer minorVersion) {
        return RESERVED_KEYWORDS.contains(identifier);
    }

    @Override
    public String quoteIdentifier(String identifier, Integer majorVersion, Integer minorVersion) {
        return quote(identifier, true);
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return quote(identifier, true);
    }

    @Override
    public String quoteIdentifierIgnoreCase(String identifier) {
        return quote(identifier, false);
    }

    @Override
    public String convertIdentifierCase(String identifier) {
        if (StringUtils.isBlank(identifier) || isQuoteIdentifier(identifier)) {
            return identifier;
        }
        return identifier.toUpperCase();
    }

    private String quote(String identifier, boolean quoteLowerCase) {
        if (StringUtils.isBlank(identifier)) {
            return identifier;
        }
        if (isQuoteIdentifier(identifier)) {
            return identifier;
        }
        if (isValidIdentifier(identifier)) {
            if ((quoteLowerCase && containsLowerCase(identifier))
                    || isReservedKeyword(identifier.toUpperCase(), null, null)) {
                return StringUtils.wrap(identifier, '"');
            }
            return identifier;
        }
        return StringUtils.wrap(identifier, '"');
    }
}
