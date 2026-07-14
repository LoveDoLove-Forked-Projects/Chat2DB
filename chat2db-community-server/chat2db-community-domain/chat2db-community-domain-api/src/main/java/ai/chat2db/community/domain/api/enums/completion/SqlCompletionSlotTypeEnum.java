package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;

public enum SqlCompletionSlotTypeEnum {
    UNKNOWN,
    TABLE_REFERENCE,
    TABLE_DECLARATION,
    COLUMN_REFERENCE,
    INSERT_VALUE_EXPRESSION,
    DATA_TYPE,
    CHARSET,
    COLLATION,
    OBJECT_REFERENCE,
    ALIAS_DECLARATION,
    ROUTINE_LOCAL_SYMBOL,
    CLAUSE_KEYWORD;

    public static SqlCompletionSlotTypeEnum from(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return UNKNOWN;
        }
    }
}
