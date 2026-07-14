package ai.chat2db.plugin.mysql.completion.catalog;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionClauseSpec;
import java.util.List;
import java.util.Set;


public final class MysqlSqlCompletionClauseCatalog {

    private static final List<MysqlSqlCompletionClauseRule> CLAUSES = List.of(
            clause(MySqlLexer.ORDER, "select-order-by", "ORDER BY", "Order rows",
                    MySqlParser.RULE_orderByClause,
                    MySqlParser.RULE_querySpecification,
                    MySqlParser.RULE_querySpecificationNointo,
                    MySqlParser.RULE_selectStatement),
            clause(MySqlLexer.GROUP, "select-group-by", "GROUP BY", "Group rows",
                    MySqlParser.RULE_groupByClause,
                    MySqlParser.RULE_querySpecification,
                    MySqlParser.RULE_querySpecificationNointo,
                    MySqlParser.RULE_selectStatement),
            boundaryClause(MySqlLexer.PRIMARY, "constraint-primary-key", "PRIMARY KEY", "Define a primary key",
                    Set.of(MySqlParser.RULE_columnConstraint, MySqlParser.RULE_tableConstraint,
                            MySqlParser.RULE_alterSpecification),
                    MySqlParser.RULE_columnConstraint,
                    MySqlParser.RULE_tableConstraint,
                    MySqlParser.RULE_alterSpecification),
            boundaryClause(MySqlLexer.UNIQUE, "constraint-unique-key", "UNIQUE KEY", "Define a unique key",
                    Set.of(MySqlParser.RULE_columnConstraint, MySqlParser.RULE_tableConstraint,
                            MySqlParser.RULE_alterSpecification),
                    MySqlParser.RULE_columnConstraint,
                    MySqlParser.RULE_tableConstraint,
                    MySqlParser.RULE_alterSpecification),
            boundaryClause(MySqlLexer.FOREIGN, "constraint-foreign-key", "FOREIGN KEY", "Define a foreign key",
                    Set.of(MySqlParser.RULE_tableConstraint),
                    MySqlParser.RULE_tableConstraint,
                    MySqlParser.RULE_alterSpecification),
            boundaryClause(MySqlLexer.NOT, "constraint-not-null", "NOT NULL", "Require a non-null value",
                    Set.of(MySqlParser.RULE_nullNotnull),
                    MySqlParser.RULE_nullNotnull,
                    MySqlParser.RULE_columnConstraint),
            boundaryClause(MySqlLexer.IS, "predicate-is-null", "IS NULL", "Compare with NULL",
                    Set.of(MySqlParser.RULE_expression, MySqlParser.RULE_predicate, MySqlParser.RULE_nullNotnull),
                    MySqlParser.RULE_expression,
                    MySqlParser.RULE_predicate,
                    MySqlParser.RULE_nullNotnull),
            boundaryClause(MySqlLexer.IS, "predicate-is-not-null", "IS NOT NULL", "Compare with non-NULL",
                    Set.of(MySqlParser.RULE_expression, MySqlParser.RULE_predicate, MySqlParser.RULE_nullNotnull),
                    MySqlParser.RULE_expression,
                    MySqlParser.RULE_predicate,
                    MySqlParser.RULE_nullNotnull),
            boundaryClause(MySqlLexer.CHARACTER, "charset-character-set", "CHARACTER SET", "Choose a character set",
                    Set.of(MySqlParser.RULE_charSet, MySqlParser.RULE_createDatabaseOption,
                            MySqlParser.RULE_tableOption, MySqlParser.RULE_setStatement,
                            MySqlParser.RULE_showCommonEntity),
                    MySqlParser.RULE_charSet,
                    MySqlParser.RULE_createDatabaseOption,
                    MySqlParser.RULE_tableOption,
                    MySqlParser.RULE_setStatement,
                    MySqlParser.RULE_showCommonEntity),
            boundaryClause(MySqlLexer.IF, "ddl-if-exists", "IF EXISTS", "Only if the object exists",
                    Set.of(MySqlParser.RULE_ifExists),
                    MySqlParser.RULE_ifExists),
            boundaryClause(MySqlLexer.IF, "ddl-if-not-exists", "IF NOT EXISTS", "Only if the object does not exist",
                    Set.of(MySqlParser.RULE_ifNotExists),
                    MySqlParser.RULE_ifNotExists),
            boundaryClause(MySqlLexer.ON, "reference-on-delete", "ON DELETE", "Define a reference delete action",
                    Set.of(MySqlParser.RULE_referenceAction),
                    MySqlParser.RULE_referenceAction),
            boundaryClause(MySqlLexer.ON, "reference-on-update", "ON UPDATE", "Define an update action",
                    Set.of(MySqlParser.RULE_referenceAction, MySqlParser.RULE_columnConstraint),
                    MySqlParser.RULE_referenceAction,
                    MySqlParser.RULE_columnConstraint),
            boundaryClause(MySqlLexer.ON, "insert-on-duplicate-key-update", "ON DUPLICATE KEY UPDATE",
                    "Handle duplicate key updates",
                    Set.of(MySqlParser.RULE_insertStatement),
                    MySqlParser.RULE_insertStatement),
            boundaryClause(MySqlLexer.LEFT, "join-left", "LEFT JOIN", "Join with rows from the left side",
                    Set.of(MySqlParser.RULE_joinPart),
                    MySqlParser.RULE_joinPart),
            boundaryClause(MySqlLexer.RIGHT, "join-right", "RIGHT JOIN", "Join with rows from the right side",
                    Set.of(MySqlParser.RULE_joinPart),
                    MySqlParser.RULE_joinPart),
            boundaryClause(MySqlLexer.INNER, "join-inner", "INNER JOIN", "Join matching rows",
                    Set.of(MySqlParser.RULE_joinPart),
                    MySqlParser.RULE_joinPart),
            boundaryClause(MySqlLexer.CROSS, "join-cross", "CROSS JOIN", "Join every row combination",
                    Set.of(MySqlParser.RULE_joinPart),
                    MySqlParser.RULE_joinPart),
            boundaryClause(MySqlLexer.NATURAL, "join-natural", "NATURAL JOIN", "Join by common columns",
                    Set.of(MySqlParser.RULE_joinPart),
                    MySqlParser.RULE_joinPart)
    );

    private MysqlSqlCompletionClauseCatalog() {
    }

    public static List<MysqlSqlCompletionClauseRule> clauses(Integer tokenType) {
        if (tokenType == null) {
            return List.of();
        }
        return CLAUSES.stream()
                .filter(rule -> rule.tokenType() == tokenType)
                .toList();
    }

    private static MysqlSqlCompletionClauseRule clause(int tokenType,
                                                       String id,
                                                       String label,
                                                       String description,
                                                       Integer... rulePathRules) {
        return clause(tokenType, id, label, description, Set.of(), rulePathRules);
    }

    private static MysqlSqlCompletionClauseRule boundaryClause(int tokenType,
                                                               String id,
                                                               String label,
                                                               String description,
                                                               Set<Integer> boundaryPreferredRules,
                                                               Integer... rulePathRules) {
        return clause(tokenType, id, label, description, boundaryPreferredRules, rulePathRules);
    }

    private static MysqlSqlCompletionClauseRule clause(int tokenType,
                                                       String id,
                                                       String label,
                                                       String description,
                                                       Set<Integer> boundaryPreferredRules,
                                                       Integer... rulePathRules) {
        return new MysqlSqlCompletionClauseRule(tokenType,
                new SqlCompletionClauseSpec(id, label, List.of(), label, description, 1000 + tokenType),
                Set.of(rulePathRules),
                boundaryPreferredRules);
    }
}
