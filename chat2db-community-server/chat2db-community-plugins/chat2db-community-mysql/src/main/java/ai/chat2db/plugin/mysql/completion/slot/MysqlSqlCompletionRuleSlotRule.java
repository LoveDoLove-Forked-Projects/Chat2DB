package ai.chat2db.plugin.mysql.completion.slot;

import ai.chat2db.plugin.mysql.completion.context.MysqlSqlCompletionCandidateContextFactory;
import ai.chat2db.plugin.mysql.enums.completion.MysqlSqlCompletionRuleSlotTypeEnum;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import ai.chat2db.spi.ISqlCompletionSlotRule;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionSlotTypeEnum;
import java.util.Optional;

final class MysqlSqlCompletionRuleSlotRule implements ISqlCompletionSlotRule {

    private final MysqlSqlCompletionCandidateContextFactory contextFactory;

    MysqlSqlCompletionRuleSlotRule(MysqlSqlCompletionCandidateContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public Optional<SqlCompletionSlot> classify(SqlCompletionPipelineState state) {
        MysqlSqlCompletionRuleSlot ruleSlot = contextFactory.create(state).ruleSlot();
        SqlCompletionSlotTypeEnum slotType = toSlotType(ruleSlot.type());
        SqlCompletionCandidateTypeEnum metadataType = ruleSlot.metadataType();
        return Optional.of(new SqlCompletionSlot(slotType.name(), metadataType == null ? null : metadataType.name(),
                null));
    }

    private static SqlCompletionSlotTypeEnum toSlotType(MysqlSqlCompletionRuleSlotTypeEnum ruleSlotType) {
        return switch (ruleSlotType == null ? MysqlSqlCompletionRuleSlotTypeEnum.UNKNOWN : ruleSlotType) {
            case TABLE_REFERENCE -> SqlCompletionSlotTypeEnum.TABLE_REFERENCE;
            case TABLE_DECLARATION -> SqlCompletionSlotTypeEnum.TABLE_DECLARATION;
            case COLUMN_REFERENCE -> SqlCompletionSlotTypeEnum.COLUMN_REFERENCE;
            case INSERT_VALUE_EXPRESSION -> SqlCompletionSlotTypeEnum.INSERT_VALUE_EXPRESSION;
            case DATA_TYPE_REFERENCE -> SqlCompletionSlotTypeEnum.DATA_TYPE;
            case CHARSET_REFERENCE -> SqlCompletionSlotTypeEnum.CHARSET;
            case COLLATION_REFERENCE -> SqlCompletionSlotTypeEnum.COLLATION;
            case ALIAS_DECLARATION -> SqlCompletionSlotTypeEnum.ALIAS_DECLARATION;
            case ROUTINE_LOCAL_SYMBOL -> SqlCompletionSlotTypeEnum.ROUTINE_LOCAL_SYMBOL;
            case DATABASE_REFERENCE,
                    EVENT_REFERENCE,
                    FUNCTION_REFERENCE,
                    PROCEDURE_REFERENCE,
                    ROLE_REFERENCE,
                    TABLESPACE_REFERENCE,
                    TRIGGER_REFERENCE,
                    USER_REFERENCE -> SqlCompletionSlotTypeEnum.OBJECT_REFERENCE;
            default -> SqlCompletionSlotTypeEnum.UNKNOWN;
        };
    }
}
