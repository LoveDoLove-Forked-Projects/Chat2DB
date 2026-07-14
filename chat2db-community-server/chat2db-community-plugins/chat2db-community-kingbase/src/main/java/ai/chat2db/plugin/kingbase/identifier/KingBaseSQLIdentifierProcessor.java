package ai.chat2db.plugin.kingbase.identifier;

import ai.chat2db.spi.DefaultSQLIdentifierProcessor;
import org.apache.commons.lang3.StringUtils;

public class KingBaseSQLIdentifierProcessor  extends DefaultSQLIdentifierProcessor {


    @Override
    public String quoteIdentifier(String identifier) {
        if (isValidIdentifier(identifier)) {
            if (containsUpperCase(identifier) || isReservedKeyword(identifier.toUpperCase(), null, null)) {
                return StringUtils.wrap(identifier, '"');
            }
            return identifier;
        }
        return StringUtils.wrap(identifier, '"');
    }

}
