package ai.chat2db.plugin.mysql.completion.plan;

import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionIntentTypeEnum;
import ai.chat2db.community.domain.api.model.completion.plan.SqlCompletionCandidatePlanItem;
import ai.chat2db.spi.ISqlCompletionPlanRule;
import java.util.Optional;

final class MysqlSqlCompletionIntentPlanRule implements ISqlCompletionPlanRule {

    @Override
    public Optional<SqlCompletionCandidatePlanItem> plan(SqlCompletionPipelineState state,
                                                         SqlCompletionIntent intent) {
        SqlCompletionIntentTypeEnum type = intent == null ? null : SqlCompletionIntentTypeEnum.from(intent.type());
        if (type == null) {
            return Optional.empty();
        }
        return Optional.of(new SqlCompletionCandidatePlanItem(intent, type.name()));
    }
}
