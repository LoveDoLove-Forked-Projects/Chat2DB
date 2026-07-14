package ai.chat2db.plugin.mysql.completion.intent;

import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import ai.chat2db.spi.ISqlCompletionIntentRule;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionIntentTypeEnum;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionSlotTypeEnum;
import java.util.List;

final class MysqlSqlCompletionSlotIntentRule implements ISqlCompletionIntentRule {

    @Override
    public List<SqlCompletionIntent> resolve(SqlCompletionPipelineState state, SqlCompletionSlot slot) {
        SqlCompletionSlotTypeEnum type = slot == null ? SqlCompletionSlotTypeEnum.UNKNOWN
                : SqlCompletionSlotTypeEnum.from(slot.type());
        return switch (type) {
            case TABLE_REFERENCE -> List.of(intent(SqlCompletionIntentTypeEnum.TABLES, slot),
                    intent(SqlCompletionIntentTypeEnum.KEYWORDS, slot),
                    intent(SqlCompletionIntentTypeEnum.SNIPPETS, slot));
            case TABLE_DECLARATION -> List.of(intent(SqlCompletionIntentTypeEnum.TABLE_QUALIFIERS, slot));
            case COLUMN_REFERENCE, INSERT_VALUE_EXPRESSION -> List.of(intent(SqlCompletionIntentTypeEnum.COLUMNS, slot),
                    intent(SqlCompletionIntentTypeEnum.LOCAL_SYMBOLS, slot),
                    intent(SqlCompletionIntentTypeEnum.KEYWORDS, slot),
                    intent(SqlCompletionIntentTypeEnum.BUILTIN_FUNCTIONS, slot),
                    intent(SqlCompletionIntentTypeEnum.SNIPPETS, slot));
            case DATA_TYPE -> List.of(intent(SqlCompletionIntentTypeEnum.DATA_TYPES, slot));
            case CHARSET -> List.of(intent(SqlCompletionIntentTypeEnum.CHARSETS, slot));
            case COLLATION -> List.of(intent(SqlCompletionIntentTypeEnum.COLLATIONS, slot));
            case OBJECT_REFERENCE -> List.of(intent(SqlCompletionIntentTypeEnum.OBJECTS, slot));
            case ROUTINE_LOCAL_SYMBOL -> List.of(intent(SqlCompletionIntentTypeEnum.LOCAL_SYMBOLS, slot));
            default -> List.of(intent(SqlCompletionIntentTypeEnum.KEYWORDS, slot),
                    intent(SqlCompletionIntentTypeEnum.DATA_TYPES, slot),
                    intent(SqlCompletionIntentTypeEnum.SNIPPETS, slot),
                    intent(SqlCompletionIntentTypeEnum.BUILTIN_FUNCTIONS, slot));
        };
    }

    private static SqlCompletionIntent intent(SqlCompletionIntentTypeEnum type, SqlCompletionSlot slot) {
        return new SqlCompletionIntent(type.name(), slot);
    }
}
