package ai.chat2db.community.domain.api.model.completion;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionSnippetSlotTypeEnum;
import java.util.Objects;


public record SqlCompletionActiveSnippetSlot(String type,
                                             Integer replaceStart,
                                             Integer replaceEnd) {

    public SqlCompletionActiveSnippetSlot {
        SqlCompletionSnippetSlotTypeEnum resolvedType = SqlCompletionSnippetSlotTypeEnum.from(type);
        type = resolvedType == null ? null : resolvedType.name();
        if (replaceStart != null) {
            replaceStart = Math.max(0, replaceStart);
        }
        if (replaceEnd != null) {
            replaceEnd = Math.max(0, replaceEnd);
        }
        if (replaceStart != null && replaceEnd != null && replaceEnd < replaceStart) {
            replaceEnd = replaceStart;
        }
    }

    public boolean active() {
        return type != null
                && Objects.nonNull(replaceStart)
                && Objects.nonNull(replaceEnd);
    }

    public boolean covers(int start, int end) {
        return active() && replaceStart <= start && replaceEnd >= end;
    }

}
