package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;

public enum SqlCompletionIntentTypeEnum {
    KEYWORDS,
    SNIPPETS,
    BUILTIN_FUNCTIONS,
    TABLES,
    TABLE_QUALIFIERS,
    COLUMNS,
    OBJECTS,
    DATA_TYPES,
    CHARSETS,
    COLLATIONS,
    LOCAL_SYMBOLS,
    INSERT_VALUE_COLUMNS;

    public static SqlCompletionIntentTypeEnum from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
