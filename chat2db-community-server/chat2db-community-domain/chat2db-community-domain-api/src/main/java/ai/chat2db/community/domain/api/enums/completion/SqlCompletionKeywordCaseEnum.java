package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;


public enum SqlCompletionKeywordCaseEnum {
    UPPER,
    LOWER;

    public static SqlCompletionKeywordCaseEnum from(String value) {
        if (value == null || value.isBlank()) {
            return LOWER;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return LOWER;
        }
    }
}
