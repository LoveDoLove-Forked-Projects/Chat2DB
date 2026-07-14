package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.evidence.SqlCompletionRuleEvidence;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import java.util.List;

/**
 * Collects evidence used to explain or score SQL completion rules.
 */
public interface ISqlCompletionRuleEvidenceCollector {

    /**
     * Collects rule evidence from the current pipeline state.
     *
     * @param state current completion pipeline state.
     * @return evidence records used by planning, ranking, or tracing.
     * <p>
     * Typical usage:
     */
    List<SqlCompletionRuleEvidence> collect(SqlCompletionPipelineState state);
}
