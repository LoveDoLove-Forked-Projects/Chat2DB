package ai.chat2db.community.domain.api.model.completion.plan;

import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;

public record SqlCompletionCandidatePlanItem(SqlCompletionIntent intent,
                                             String providerId) {
}
