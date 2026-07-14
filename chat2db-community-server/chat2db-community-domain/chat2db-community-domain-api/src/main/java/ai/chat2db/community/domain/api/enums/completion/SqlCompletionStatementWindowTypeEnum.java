package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;


public enum SqlCompletionStatementWindowTypeEnum {
    CURRENT_STATEMENT,
    EMPTY_STATEMENT,
    BETWEEN_STATEMENTS,
    UNSUPPORTED_CONTEXT;

    public static SqlCompletionStatementWindowTypeEnum from(String value) {
        if (value == null || value.isBlank()) {
            return UNSUPPORTED_CONTEXT;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return UNSUPPORTED_CONTEXT;
        }
    }
}
