package ai.chat2db.community.domain.core.util;


import ai.chat2db.community.domain.api.model.request.db.DbTableQueryRequest;
import ai.chat2db.spi.ISQLIdentifierProcessor;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.apache.commons.lang3.StringUtils;

public class MetaNameUtils {

    public static String getMetaName(String tableName) {
        if(StringUtils.isBlank(tableName)){
            return tableName;
        }
        if(tableName.startsWith("`") && tableName.endsWith("`")){
            return tableName.substring(1,tableName.length()-1);
        }
        if(tableName.startsWith("\"") && tableName.endsWith("\"")){
            return tableName.substring(1,tableName.length()-1);
        }
        if(tableName.startsWith("'") && tableName.endsWith("'")){
            return tableName.substring(1,tableName.length()-1);
        }
        if(tableName.startsWith("[") && tableName.endsWith("]")){
            return tableName.substring(1,tableName.length()-1);
        }
        return tableName;
    }

    public static void buildRequest(DbTableQueryRequest request, String tableName) {
        tableName = tableName == null ? "" : tableName;
        String[] names = tableName.split("\\.");
        if (StringUtils.isBlank(tableName)) {
            return ;
        }
        DBConfig dbConfig = Chat2DBContext.getDBConfig();
        boolean supportSchema = dbConfig == null || dbConfig.isSupportSchema();
        boolean supportDatabase = dbConfig == null || dbConfig.isSupportDatabase();
        if (names.length > 1) {
            if (supportSchema) {
                request.setSchemaName(normalizeIdentifier(names[names.length - 2]));
            }
            if (supportDatabase && !supportSchema) {
                request.setDatabaseName(normalizeIdentifier(names[names.length - 2]));
            }
            if (supportDatabase && supportSchema && names.length > 2) {
                request.setDatabaseName(normalizeIdentifier(names[names.length - 3]));
                request.setSchemaName(normalizeIdentifier(names[names.length - 2]));
            }
        }
        request.setTableName(normalizeIdentifier(names[names.length - 1]));
        request.setDatabaseName(supportDatabase ? normalizeIdentifier(request.getDatabaseName()) : null);
        request.setSchemaName(supportSchema ? normalizeIdentifier(request.getSchemaName()) : null);
    }

    private static String normalizeIdentifier(String identifier) {
        if (StringUtils.isBlank(identifier)) {
            return identifier;
        }
        String trimmed = identifier.trim();
        if (isWrapped(trimmed, "\"", "\"")
                || isWrapped(trimmed, "`", "`")
                || isWrapped(trimmed, "'", "'")
                || isWrapped(trimmed, "[", "]")) {
            return stripIdentifierQuote(trimmed);
        }
        if (Chat2DBContext.getConnectInfo() != null) {
            ISQLIdentifierProcessor processor = Chat2DBContext.getDbMetaData().getSQLIdentifierProcessor();
            if (processor != null) {
                return processor.convertIdentifierCase(trimmed);
            }
        }
        return trimmed;
    }

    private static String stripIdentifierQuote(String identifier) {
        if (isWrapped(identifier, "\"", "\"")
                || isWrapped(identifier, "`", "`")
                || isWrapped(identifier, "'", "'")
                || isWrapped(identifier, "[", "]")) {
            return identifier.substring(1, identifier.length() - 1);
        }
        return identifier;
    }

    private static boolean isWrapped(String value, String prefix, String suffix) {
        return value.length() >= prefix.length() + suffix.length()
                && value.startsWith(prefix)
                && value.endsWith(suffix);
    }
}
