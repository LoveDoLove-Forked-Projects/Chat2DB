package ai.chat2db.plugin.mysql.model.completion.context;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import org.apache.commons.lang3.StringUtils;


public record MysqlSqlCompletionInsertColumnListContext(boolean active,
                                                        String table,
                                                        Set<String> writtenColumnIdentities) {

    private static final MysqlSqlCompletionInsertColumnListContext INACTIVE =
            new MysqlSqlCompletionInsertColumnListContext(false, null, Set.of());

    public MysqlSqlCompletionInsertColumnListContext {
        table = normalizeIdentifier(table);
        writtenColumnIdentities = writtenColumnIdentities == null
                ? Set.of()
                : Set.copyOf(writtenColumnIdentities);
    }

    public static MysqlSqlCompletionInsertColumnListContext inactive() {
        return INACTIVE;
    }

    public static MysqlSqlCompletionInsertColumnListContext active(String table, List<String> writtenColumns) {
        Set<String> identities = writtenColumns == null
                ? Set.of()
                : writtenColumns.stream()
                        .map(MysqlSqlCompletionInsertColumnListContext::normalizeIdentifier)
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toUnmodifiableSet());
        if (StringUtils.isBlank(table)) {
            return inactive();
        }
        return new MysqlSqlCompletionInsertColumnListContext(true, table, identities);
    }

    public boolean hasWrittenColumn(String column) {
        String identity = normalizeIdentifier(column);
        return StringUtils.isNotBlank(identity) && writtenColumnIdentities.contains(identity);
    }

    public boolean matchesTable(MysqlSqlCompletionRelationScope.Relation relation) {
        if (!active || relation == null || StringUtils.isBlank(table)) {
            return false;
        }
        return table.equals(normalizeIdentifier(relation.table()));
    }

    public static String normalizeIdentifier(String value) {
        String stripped = MysqlSqlCompletionTokenUtil.stripIdentifierQuotes(
                MysqlSqlCompletionTokenUtil.stripLeadingDot(value));
        return stripped.toLowerCase(Locale.ROOT);
    }
}
