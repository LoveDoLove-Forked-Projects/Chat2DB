package ai.chat2db.community.domain.api.enums.completion;

import java.util.Locale;


public enum SqlCompletionSnippetSlotTypeEnum {
    SELECT_FUNCTION,
    CALL_PROCEDURE,
    INSERT_COLUMN_LIST;

    public static SqlCompletionSnippetSlotTypeEnum from(String value) {
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
