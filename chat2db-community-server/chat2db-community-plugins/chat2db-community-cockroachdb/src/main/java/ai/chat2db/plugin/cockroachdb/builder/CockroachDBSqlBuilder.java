package ai.chat2db.plugin.cockroachdb.builder;

import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.plugin.postgresql.builder.PostgreSQLSqlBuilder;
import ai.chat2db.plugin.postgresql.identifier.PostgreSQLIdentifierProcessor;
import org.apache.commons.lang3.StringUtils;

public class CockroachDBSqlBuilder extends PostgreSQLSqlBuilder {

    @Override
    public String buildCreateDatabase(Database database) {
        String name = PostgreSQLIdentifierProcessor.INSTANCE.quoteIdentifierAlways(database.getName());
        String sql = "CREATE DATABASE " + name;
        if (StringUtils.isNotBlank(database.getComment())) {
            sql += "; COMMENT ON DATABASE " + name + " IS '"
                    + PostgreSQLIdentifierProcessor.INSTANCE.escapeString(database.getComment()) + "';";
        }
        return sql;
    }
    private static final String SQL_WHERE_ROWID_IN_OPEN_PAREN_SELECT_ROWID_FROM =
            " where rowid in (select rowid from ";
    private static final String VALUE_LIMIT_1_CLOSE_PAREN = " limit 1)";

    @Override
    protected String appendSingleRowLimit(String operationType, String tableName, String whereClause, String sql) {
        if (StringUtils.isBlank(whereClause) || !sql.endsWith(whereClause)) {
            return sql;
        }
        String body = sql.substring(0, sql.length() - whereClause.length());
        return body + SQL_WHERE_ROWID_IN_OPEN_PAREN_SELECT_ROWID_FROM + tableName + whereClause
                + VALUE_LIMIT_1_CLOSE_PAREN;
    }
}
