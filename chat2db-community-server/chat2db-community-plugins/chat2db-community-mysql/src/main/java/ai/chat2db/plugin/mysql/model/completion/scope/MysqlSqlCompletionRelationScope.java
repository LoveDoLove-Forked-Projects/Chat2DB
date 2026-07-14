package ai.chat2db.plugin.mysql.model.completion.scope;

import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;


public record MysqlSqlCompletionRelationScope(List<Relation> relations) {

    public MysqlSqlCompletionRelationScope {
        relations = relations == null ? List.of() : List.copyOf(relations);
    }

    public static MysqlSqlCompletionRelationScope empty() {
        return new MysqlSqlCompletionRelationScope(List.of());
    }

    public List<Relation> resolveOwner(String owner) {
        if (StringUtils.isBlank(owner)) {
            return List.of();
        }
        return relations.stream()
                .filter(relation -> relation.matches(owner))
                .toList();
    }

    public record Relation(String catalog,
                           String schema,
                           String table,
                           String alias,
                           List<String> columns,
                           boolean local) {

        public Relation {
            catalog = blankToNull(catalog);
            schema = blankToNull(schema);
            table = blankToNull(stripLeadingDot(table));
            alias = blankToNull(stripLeadingDot(alias));
            columns = columns == null ? List.of() : List.copyOf(columns);
        }

        public Relation(String catalog, String schema, String table, String alias, List<String> columns) {
            this(catalog, schema, table, alias, columns, false);
        }

        public Relation(String catalog, String schema, String table, String alias) {
            this(catalog, schema, table, alias, List.of(), false);
        }

        public boolean hasLocalColumns() {
            return local && !columns.isEmpty();
        }

        public boolean matches(String owner) {
            String normalizedOwner = normalize(owner);
            return StringUtils.isNotBlank(normalizedOwner)
                    && (normalizedOwner.equals(normalize(alias)) || normalizedOwner.equals(normalize(table)));
        }

        public static Relation local(String name, String alias, List<String> columns) {
            return new Relation(null, null, name, alias, columns, true);
        }

        private static String blankToNull(String value) {
            return StringUtils.isBlank(value) ? null : value;
        }

        private static String normalize(String value) {
            return stripIdentifierQuote(Objects.toString(value, "")).toLowerCase();
        }

        private static String stripIdentifierQuote(String value) {
            if (value == null) {
                return "";
            }
            String stripped = value.trim();
            if (stripped.length() >= 2 && stripped.startsWith("`") && stripped.endsWith("`")) {
                return stripped.substring(1, stripped.length() - 1);
            }
            return stripped;
        }

        private static String stripLeadingDot(String value) {
            if (value == null) {
                return "";
            }
            String stripped = value.trim();
            return stripped.startsWith(".") ? stripped.substring(1) : stripped;
        }
    }
}
