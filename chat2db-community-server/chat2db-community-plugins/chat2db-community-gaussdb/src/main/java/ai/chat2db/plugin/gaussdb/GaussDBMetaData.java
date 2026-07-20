package ai.chat2db.plugin.gaussdb;

import ai.chat2db.plugin.postgresql.PostgreSQLMetaData;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.spi.DefaultSQLExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;

import static ai.chat2db.plugin.gaussdb.constant.GaussDBMetaDataConstants.SEARCH_PATH_STATEMENT_PREFIX;
import static ai.chat2db.plugin.gaussdb.constant.GaussDBMetaDataConstants.TABLE_DDL_SQL;

@Slf4j
public class GaussDBMetaData extends PostgreSQLMetaData implements IDbMetaData {
    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        String ddl = DefaultSQLExecutor.getInstance().preExecute(connection, TABLE_DDL_SQL,
                new String[]{schemaName, tableName}, resultSet -> resultSet.next() ? resultSet.getString(1) : null);
        return stripLeadingSearchPath(ddl);
    }

    static String stripLeadingSearchPath(String ddl) {
        if (ddl == null) {
            return null;
        }

        int statementStart = 0;
        while (statementStart < ddl.length() && Character.isWhitespace(ddl.charAt(statementStart))) {
            statementStart++;
        }

        if (!ddl.regionMatches(true, statementStart, SEARCH_PATH_STATEMENT_PREFIX, 0,
                SEARCH_PATH_STATEMENT_PREFIX.length())) {
            return ddl;
        }

        int prefixEnd = statementStart + SEARCH_PATH_STATEMENT_PREFIX.length();
        if (prefixEnd < ddl.length() && Character.isJavaIdentifierPart(ddl.charAt(prefixEnd))) {
            return ddl;
        }

        int statementEnd = findStatementEnd(ddl, prefixEnd);
        if (statementEnd < 0) {
            return ddl;
        }

        int ddlStart = statementEnd + 1;
        while (ddlStart < ddl.length() && Character.isWhitespace(ddl.charAt(ddlStart))) {
            ddlStart++;
        }
        return ddl.substring(ddlStart);
    }

    private static int findStatementEnd(String sql, int start) {
        char quote = 0;
        for (int i = start; i < sql.length(); i++) {
            char current = sql.charAt(i);
            if (quote != 0) {
                if (current == quote) {
                    if (i + 1 < sql.length() && sql.charAt(i + 1) == quote) {
                        i++;
                    } else {
                        quote = 0;
                    }
                }
            } else if (current == '\'' || current == '"') {
                quote = current;
            } else if (current == ';') {
                return i;
            }
        }
        return -1;
    }

    @Override
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        return DefaultSQLExecutor.getInstance().indexes(connection, StringUtils.isEmpty(databaseName) ? null : databaseName, StringUtils.isEmpty(schemaName) ? null : schemaName, tableName);
    }


}
