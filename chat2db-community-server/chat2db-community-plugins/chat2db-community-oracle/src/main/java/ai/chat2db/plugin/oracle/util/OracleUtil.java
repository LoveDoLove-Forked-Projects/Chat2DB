package ai.chat2db.plugin.oracle.util;

import ai.chat2db.plugin.oracle.OracleMetaData;
import ai.chat2db.spi.ISQLIdentifierProcessor;
import org.apache.commons.lang3.StringUtils;

public class OracleUtil {

    public static String quoteIdentifier(String identifier) {
        ISQLIdentifierProcessor oracleSqlIdentifierProcessor = OracleMetaData.ORACLE_SQL_IDENTIFIER_PROCESSOR;
        if (StringUtils.isBlank(identifier)) {
            return identifier;
        }
        return oracleSqlIdentifierProcessor.quoteIdentifier(identifier);
    }

    public static String quoteIdentifierIgnoreCase(String identifier) {
        ISQLIdentifierProcessor oracleSqlIdentifierProcessor = OracleMetaData.ORACLE_SQL_IDENTIFIER_PROCESSOR;
        if (StringUtils.isBlank(identifier)) {
            return identifier;
        }
        return oracleSqlIdentifierProcessor.quoteIdentifierIgnoreCase(identifier);
    }

}
