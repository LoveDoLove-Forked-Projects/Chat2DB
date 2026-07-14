package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;

public enum SqlCompletionEvidenceSourceTypeEnum {
    C3_TOKEN,
    C3_RULE,
    C3_RULE_PATH,
    DUMMY_SQL,
    DIALECT_RULE;

    public static SqlCompletionEvidenceSourceTypeEnum from(String value) {
        if (value == null || value.isBlank()) {
            return DIALECT_RULE;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return DIALECT_RULE;
        }
    }
}
