package ai.chat2db.community.domain.api.model.completion.intent;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionIntentTypeEnum;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;

public record SqlCompletionIntent(String type,
                                  SqlCompletionSlot slot) {

    public SqlCompletionIntent {
        SqlCompletionIntentTypeEnum resolvedType = SqlCompletionIntentTypeEnum.from(type);
        type = resolvedType == null ? null : resolvedType.name();
    }
}
