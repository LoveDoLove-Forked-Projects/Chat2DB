package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;

public enum SqlCompletionEvidenceStrengthTypeEnum {
    STRONG,
    MEDIUM,
    WEAK;

    public static SqlCompletionEvidenceStrengthTypeEnum from(String value) {
        if (value == null || value.isBlank()) {
            return MEDIUM;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return MEDIUM;
        }
    }
}
