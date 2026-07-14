package ai.chat2db.plugin.mysql.model.completion.scope;

import java.util.List;


public record MysqlSqlCompletionLocalColumnScope(boolean applies,
                                                String table,
                                                List<String> columns) {

    public MysqlSqlCompletionLocalColumnScope {
        columns = columns == null ? List.of() : List.copyOf(columns);
    }

    public static MysqlSqlCompletionLocalColumnScope notApplicable() {
        return new MysqlSqlCompletionLocalColumnScope(false, null, List.of());
    }
}
