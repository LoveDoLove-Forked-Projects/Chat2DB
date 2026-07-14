package ai.chat2db.community.domain.api.model.completion.context;

import java.util.Objects;

public record SqlCompletionLocalColumn(String name,
                                       String dataType,
                                       SqlCompletionSourceSpan sourceSpan) {

    public SqlCompletionLocalColumn {
        name = Objects.toString(name, "").trim();
        dataType = blankToNull(dataType);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
