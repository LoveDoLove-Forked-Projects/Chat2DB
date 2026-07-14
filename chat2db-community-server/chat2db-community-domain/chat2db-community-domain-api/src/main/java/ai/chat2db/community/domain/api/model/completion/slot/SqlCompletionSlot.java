package ai.chat2db.community.domain.api.model.completion.slot;

import ai.chat2db.community.domain.api.model.completion.evidence.SqlCompletionRuleEvidence;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionSlotTypeEnum;

public record SqlCompletionSlot(String type,
                                String metadataType,
                                SqlCompletionRuleEvidence evidence) {

    public SqlCompletionSlot {
        type = SqlCompletionSlotTypeEnum.from(type).name();
        metadataType = metadataType == null ? null : SqlCompletionCandidateTypeEnum.from(metadataType).name();
    }

    public static SqlCompletionSlot unknown() {
        return new SqlCompletionSlot(SqlCompletionSlotTypeEnum.UNKNOWN.name(), null, null);
    }
}
