package ai.chat2db.community.domain.api.model.completion.evidence;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionEvidenceSourceTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionEvidenceStrengthTypeEnum;
import java.util.List;

public record SqlCompletionRuleEvidence(String source,
                                        String strength,
                                        Integer tokenType,
                                        Integer rule,
                                        List<Integer> rulePath,
                                        int startTokenIndex,
                                        int stopTokenIndex) {

    public SqlCompletionRuleEvidence {
        source = SqlCompletionEvidenceSourceTypeEnum.from(source).name();
        strength = SqlCompletionEvidenceStrengthTypeEnum.from(strength).name();
        rulePath = rulePath == null ? List.of() : List.copyOf(rulePath);
    }
}
