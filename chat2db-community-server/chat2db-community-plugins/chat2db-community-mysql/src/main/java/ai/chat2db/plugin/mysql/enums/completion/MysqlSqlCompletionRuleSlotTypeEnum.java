package ai.chat2db.plugin.mysql.enums.completion;

import ai.chat2db.mysql.parser.base.MySqlParser;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


public enum MysqlSqlCompletionRuleSlotTypeEnum {
    UNKNOWN(-1),
    DATABASE_REFERENCE(MySqlParser.RULE_databaseReferenceName),
    DATABASE_DECLARATION(MySqlParser.RULE_databaseDeclarationName),
    EVENT_REFERENCE(MySqlParser.RULE_eventReferenceName),
    EVENT_DECLARATION(MySqlParser.RULE_eventDeclarationName),
    FUNCTION_REFERENCE(MySqlParser.RULE_functionReferenceName),
    FUNCTION_DECLARATION(MySqlParser.RULE_functionDeclarationName),
    PROCEDURE_REFERENCE(MySqlParser.RULE_procedureReferenceName),
    PROCEDURE_DECLARATION(MySqlParser.RULE_procedureDeclarationName),
    ROLE_REFERENCE(MySqlParser.RULE_roleReferenceName),
    ROLE_DECLARATION(MySqlParser.RULE_roleDeclarationName),
    TABLESPACE_REFERENCE(MySqlParser.RULE_tablespaceReferenceName),
    TABLESPACE_DECLARATION(MySqlParser.RULE_tablespaceDeclarationName),
    TRIGGER_REFERENCE(MySqlParser.RULE_triggerReferenceName),
    TRIGGER_DECLARATION(MySqlParser.RULE_triggerDeclarationName),
    USER_REFERENCE(MySqlParser.RULE_userReferenceName),
    USER_DECLARATION(MySqlParser.RULE_userDeclarationName),
    TABLE_REFERENCE(MySqlParser.RULE_tableReferenceName, MySqlParser.RULE_viewReferenceName),
    TABLE_DECLARATION(MySqlParser.RULE_tableDeclarationName, MySqlParser.RULE_viewDeclarationName),
    COLUMN_REFERENCE(MySqlParser.RULE_columnReferenceName),
    CHARSET_REFERENCE(MySqlParser.RULE_charsetName),
    COLLATION_REFERENCE(MySqlParser.RULE_collationName),
    DATA_TYPE_REFERENCE(MySqlParser.RULE_dataType, MySqlParser.RULE_dataTypeBase),
    INSERT_VALUE_EXPRESSION(MySqlParser.RULE_insertValueExpressions, MySqlParser.RULE_insertValueExpression),
    ROUTINE_LOCAL_SYMBOL(MySqlParser.RULE_variableClause),
    COLUMN_DECLARATION(MySqlParser.RULE_columnDeclarationName),
    ALIAS_DECLARATION(MySqlParser.RULE_aliasDeclarationName);

    private final Set<Integer> rules;

    MysqlSqlCompletionRuleSlotTypeEnum(int... rules) {
        this.rules = Arrays.stream(rules).boxed().collect(Collectors.toUnmodifiableSet());
    }

    public int rule() {
        return rules.stream().findFirst().orElse(-1);
    }

    public static MysqlSqlCompletionRuleSlotTypeEnum fromRule(int rule) {
        return Arrays.stream(values())
                .filter(type -> type.rules.contains(rule))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public static MysqlSqlCompletionRuleSlotTypeEnum fromRulePath(Collection<Integer> rulePath) {
        if (rulePath == null || rulePath.isEmpty()) {
            return UNKNOWN;
        }
        return rulePath.stream()
                .map(MysqlSqlCompletionRuleSlotTypeEnum::fromRule)
                .filter(type -> type != UNKNOWN)
                .reduce((left, right) -> right)
                .orElse(UNKNOWN);
    }
}
