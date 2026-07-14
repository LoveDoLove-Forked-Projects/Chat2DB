package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;


public enum SqlCompletionDummyTypeEnum {
    NONE,
    DANGLING_DOT_IDENTIFIER,
    IDENTIFIER,
    DATA_TYPE,
    EXPRESSION,
    CLOSE_PAREN,
    QUOTED_IDENTIFIER;

    public static SqlCompletionDummyTypeEnum from(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return NONE;
        }
    }
}
