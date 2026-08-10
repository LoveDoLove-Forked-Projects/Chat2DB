package ai.chat2db.community.domain.api.service.db.extension;

import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionPlan;
import ai.chat2db.community.domain.api.model.sql.extension.SqlResultColumnContext;

public interface ISqlExecutionPolicy {

    default String rewriteSql(SqlExecutionContext context, String sql) {
        return sql;
    }

    default Integer maxRows(SqlExecutionContext context, String sql) {
        return null;
    }

    default void beforeExecute(SqlExecutionPlan plan) {
    }

    default boolean includeColumn(SqlResultColumnContext context) {
        return true;
    }
}
