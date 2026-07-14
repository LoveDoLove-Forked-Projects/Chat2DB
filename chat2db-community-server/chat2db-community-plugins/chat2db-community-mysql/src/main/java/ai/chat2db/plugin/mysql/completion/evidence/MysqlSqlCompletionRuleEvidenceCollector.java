package ai.chat2db.plugin.mysql.completion.evidence;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionEvidenceSourceTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionEvidenceStrengthTypeEnum;
import ai.chat2db.community.domain.api.model.completion.evidence.SqlCompletionRuleEvidence;
import ai.chat2db.spi.ISqlCompletionRuleEvidenceCollector;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class MysqlSqlCompletionRuleEvidenceCollector implements ISqlCompletionRuleEvidenceCollector {

    @Override
    public List<SqlCompletionRuleEvidence> collect(SqlCompletionPipelineState state) {
        SqlCompletionCandidates c3Result = state.c3Result();
        if (c3Result == null || !c3Result.available()) {
            return List.of();
        }
        List<SqlCompletionRuleEvidence> evidence = new ArrayList<>();
        c3Result.tokens().forEach((tokenType, rulePath) -> evidence.add(new SqlCompletionRuleEvidence(
                SqlCompletionEvidenceSourceTypeEnum.C3_TOKEN.name(),
                SqlCompletionEvidenceStrengthTypeEnum.STRONG.name(),
                tokenType,
                null,
                rulePath,
                c3Result.tokenIndex(),
                c3Result.tokenIndex())));
        for (Map.Entry<Integer, SqlCompletionCandidates.RuleCandidate> entry : c3Result.rules().entrySet()) {
            SqlCompletionCandidates.RuleCandidate candidate = entry.getValue();
            if (candidate == null) {
                continue;
            }
            evidence.add(new SqlCompletionRuleEvidence(SqlCompletionEvidenceSourceTypeEnum.C3_RULE.name(),
                    SqlCompletionEvidenceStrengthTypeEnum.STRONG.name(),
                    null,
                    entry.getKey(),
                    candidate.ruleList(),
                    candidate.startTokenIndex(),
                    candidate.stopTokenIndex()));
        }
        return evidence;
    }
}
