package ai.chat2db.community.domain.api.model.completion;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public record SqlCompletionClauseSpec(String id,
                                      String label,
                                      List<String> matchTexts,
                                      String insertText,
                                      String description,
                                      int sortRank) {

    public SqlCompletionClauseSpec {
        matchTexts = normalizeMatchTexts(label, matchTexts);
    }

    public boolean matchesPrefix(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return true;
        }
        return matchTexts.stream().anyMatch(matchText -> StringUtils.startsWithIgnoreCase(matchText, prefix));
    }

    private static List<String> normalizeMatchTexts(String label, List<String> matchTexts) {
        List<String> result = new ArrayList<>();
        addMatchText(result, label);
        if (matchTexts != null) {
            matchTexts.forEach(matchText -> addMatchText(result, matchText));
        }
        return List.copyOf(result);
    }

    private static void addMatchText(List<String> result, String matchText) {
        if (StringUtils.isBlank(matchText) || result.contains(matchText)) {
            return;
        }
        result.add(matchText);
    }
}
