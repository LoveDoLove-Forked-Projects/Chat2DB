package ai.chat2db.plugin.oracle.identifier;

import ai.chat2db.spi.DefaultSQLIdentifierProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;


public class OracleIdentifierProcessor extends DefaultSQLIdentifierProcessor {

    private static final Set<String> ORACLE_RESERVED_KEYWORDS = new HashSet<>();

    static {
        ORACLE_RESERVED_KEYWORDS.add("ACCESS");
        ORACLE_RESERVED_KEYWORDS.add("ADD");
        ORACLE_RESERVED_KEYWORDS.add("ALL");
        ORACLE_RESERVED_KEYWORDS.add("ALTER");
        ORACLE_RESERVED_KEYWORDS.add("AND");
        ORACLE_RESERVED_KEYWORDS.add("ANY");
        ORACLE_RESERVED_KEYWORDS.add("AS");
        ORACLE_RESERVED_KEYWORDS.add("ASC");
        ORACLE_RESERVED_KEYWORDS.add("AUDIT");
        ORACLE_RESERVED_KEYWORDS.add("BETWEEN");
        ORACLE_RESERVED_KEYWORDS.add("BY");
        ORACLE_RESERVED_KEYWORDS.add("CHAR");
        ORACLE_RESERVED_KEYWORDS.add("CHECK");
        ORACLE_RESERVED_KEYWORDS.add("CLUSTER");
        ORACLE_RESERVED_KEYWORDS.add("COLUMN");
        ORACLE_RESERVED_KEYWORDS.add("COLUMN_VALUE");
        ORACLE_RESERVED_KEYWORDS.add("COMMENT");
        ORACLE_RESERVED_KEYWORDS.add("COMPRESS");
        ORACLE_RESERVED_KEYWORDS.add("CONNECT");
        ORACLE_RESERVED_KEYWORDS.add("CREATE");
        ORACLE_RESERVED_KEYWORDS.add("CURRENT");
        ORACLE_RESERVED_KEYWORDS.add("DATE");
        ORACLE_RESERVED_KEYWORDS.add("DECIMAL");
        ORACLE_RESERVED_KEYWORDS.add("DEFAULT");
        ORACLE_RESERVED_KEYWORDS.add("DELETE");
        ORACLE_RESERVED_KEYWORDS.add("DESC");
        ORACLE_RESERVED_KEYWORDS.add("DISTINCT");
        ORACLE_RESERVED_KEYWORDS.add("DROP");
        ORACLE_RESERVED_KEYWORDS.add("ELSE");
        ORACLE_RESERVED_KEYWORDS.add("EXCLUSIVE");
        ORACLE_RESERVED_KEYWORDS.add("EXISTS");
        ORACLE_RESERVED_KEYWORDS.add("FILE");
        ORACLE_RESERVED_KEYWORDS.add("FLOAT");
        ORACLE_RESERVED_KEYWORDS.add("FOR");
        ORACLE_RESERVED_KEYWORDS.add("FROM");
        ORACLE_RESERVED_KEYWORDS.add("GRANT");
        ORACLE_RESERVED_KEYWORDS.add("GROUP");
        ORACLE_RESERVED_KEYWORDS.add("HAVING");
        ORACLE_RESERVED_KEYWORDS.add("IDENTIFIED");
        ORACLE_RESERVED_KEYWORDS.add("IMMEDIATE");
        ORACLE_RESERVED_KEYWORDS.add("IN");
        ORACLE_RESERVED_KEYWORDS.add("INCREMENT");
        ORACLE_RESERVED_KEYWORDS.add("INDEX");
        ORACLE_RESERVED_KEYWORDS.add("INITIAL");
        ORACLE_RESERVED_KEYWORDS.add("INSERT");
        ORACLE_RESERVED_KEYWORDS.add("INTEGER");
        ORACLE_RESERVED_KEYWORDS.add("INTERSECT");
        ORACLE_RESERVED_KEYWORDS.add("INTO");
        ORACLE_RESERVED_KEYWORDS.add("IS");
        ORACLE_RESERVED_KEYWORDS.add("LEVEL");
        ORACLE_RESERVED_KEYWORDS.add("LIKE");
        ORACLE_RESERVED_KEYWORDS.add("LOCK");
        ORACLE_RESERVED_KEYWORDS.add("LONG");
        ORACLE_RESERVED_KEYWORDS.add("MAXEXTENTS");
        ORACLE_RESERVED_KEYWORDS.add("MINUS");
        ORACLE_RESERVED_KEYWORDS.add("MLSLABEL");
        ORACLE_RESERVED_KEYWORDS.add("MODE");
        ORACLE_RESERVED_KEYWORDS.add("MODIFY");
        ORACLE_RESERVED_KEYWORDS.add("NESTED_TABLE_ID");
        ORACLE_RESERVED_KEYWORDS.add("NOAUDIT");
        ORACLE_RESERVED_KEYWORDS.add("NOCOMPRESS");
        ORACLE_RESERVED_KEYWORDS.add("NOT");
        ORACLE_RESERVED_KEYWORDS.add("NOWAIT");
        ORACLE_RESERVED_KEYWORDS.add("NULL");
        ORACLE_RESERVED_KEYWORDS.add("NUMBER");
        ORACLE_RESERVED_KEYWORDS.add("OF");
        ORACLE_RESERVED_KEYWORDS.add("OFFLINE");
        ORACLE_RESERVED_KEYWORDS.add("ON");
        ORACLE_RESERVED_KEYWORDS.add("ONLINE");
        ORACLE_RESERVED_KEYWORDS.add("OPTION");
        ORACLE_RESERVED_KEYWORDS.add("OR");
        ORACLE_RESERVED_KEYWORDS.add("ORDER");
        ORACLE_RESERVED_KEYWORDS.add("PCTFREE");
        ORACLE_RESERVED_KEYWORDS.add("PRIOR");
        ORACLE_RESERVED_KEYWORDS.add("PUBLIC");
        ORACLE_RESERVED_KEYWORDS.add("RAW");
        ORACLE_RESERVED_KEYWORDS.add("RENAME");
        ORACLE_RESERVED_KEYWORDS.add("RESOURCE");
        ORACLE_RESERVED_KEYWORDS.add("REVOKE");
        ORACLE_RESERVED_KEYWORDS.add("ROW");
        ORACLE_RESERVED_KEYWORDS.add("ROWID");
        ORACLE_RESERVED_KEYWORDS.add("ROWNUM");
        ORACLE_RESERVED_KEYWORDS.add("ROWS");
        ORACLE_RESERVED_KEYWORDS.add("SELECT");
        ORACLE_RESERVED_KEYWORDS.add("SESSION");
        ORACLE_RESERVED_KEYWORDS.add("SET");
        ORACLE_RESERVED_KEYWORDS.add("SHARE");
        ORACLE_RESERVED_KEYWORDS.add("SIZE");
        ORACLE_RESERVED_KEYWORDS.add("SMALLINT");
        ORACLE_RESERVED_KEYWORDS.add("START");
        ORACLE_RESERVED_KEYWORDS.add("SUCCESSFUL");
        ORACLE_RESERVED_KEYWORDS.add("SYNONYM");
        ORACLE_RESERVED_KEYWORDS.add("SYSDATE");
        ORACLE_RESERVED_KEYWORDS.add("TABLE");
        ORACLE_RESERVED_KEYWORDS.add("THEN");
        ORACLE_RESERVED_KEYWORDS.add("TO");
        ORACLE_RESERVED_KEYWORDS.add("TRIGGER");
        ORACLE_RESERVED_KEYWORDS.add("UID");
        ORACLE_RESERVED_KEYWORDS.add("UNION");
        ORACLE_RESERVED_KEYWORDS.add("UNIQUE");
        ORACLE_RESERVED_KEYWORDS.add("UPDATE");
        ORACLE_RESERVED_KEYWORDS.add("USER");
        ORACLE_RESERVED_KEYWORDS.add("VALIDATE");
        ORACLE_RESERVED_KEYWORDS.add("VALUES");
        ORACLE_RESERVED_KEYWORDS.add("VARCHAR");
        ORACLE_RESERVED_KEYWORDS.add("VARCHAR2");
        ORACLE_RESERVED_KEYWORDS.add("VIEW");
        ORACLE_RESERVED_KEYWORDS.add("WHENEVER");
        ORACLE_RESERVED_KEYWORDS.add("WHERE");
        ORACLE_RESERVED_KEYWORDS.add("WITH");
    }

    @Override
    public boolean isReservedKeyword(String identifier, Integer majorVersion, Integer minorVersion) {
        return ORACLE_RESERVED_KEYWORDS.contains(identifier);
    }

    @Override
    public String quoteIdentifier(String identifier, Integer majorVersion, Integer minorVersion) {
        if (isValidIdentifier(identifier)) {
            if (containsLowerCase(identifier) || isReservedKeyword(identifier.toUpperCase(), majorVersion, minorVersion)) {
                return StringUtils.wrap(identifier, '"');
            }
            return identifier;
        }
        return StringUtils.wrap(identifier, '"');

    }

    @Override
    public String quoteIdentifier(String identifier) {
        if (isValidIdentifier(identifier)) {
            if (containsLowerCase(identifier) || isReservedKeyword(identifier.toUpperCase(), null, null)) {
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
        }else {
            return identifier.toUpperCase();
        }
    }

}
