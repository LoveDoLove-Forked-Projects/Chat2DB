package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;

public enum SqlCompletionLocalSymbolSourceTypeEnum {
    CURRENT_STATEMENT,
    DRAFT_DDL,
    USER_VARIABLE,
    ROUTINE_LOCAL;

    public static SqlCompletionLocalSymbolSourceTypeEnum from(String value) {
        return from(value, CURRENT_STATEMENT);
    }

    public static SqlCompletionLocalSymbolSourceTypeEnum from(String value,
                                                              SqlCompletionLocalSymbolSourceTypeEnum fallback) {
        SqlCompletionLocalSymbolSourceTypeEnum resolvedFallback = fallback == null ? CURRENT_STATEMENT : fallback;
        if (value == null || value.isBlank()) {
            return resolvedFallback;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return resolvedFallback;
        }
    }
}
