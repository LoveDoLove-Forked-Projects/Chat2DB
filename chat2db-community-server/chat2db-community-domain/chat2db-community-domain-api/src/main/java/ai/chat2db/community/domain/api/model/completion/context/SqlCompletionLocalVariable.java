package ai.chat2db.community.domain.api.model.completion.context;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionLocalSymbolSourceTypeEnum;
import java.util.Objects;

public record SqlCompletionLocalVariable(String name,
                                         String dataType,
                                         String sourceType,
                                         SqlCompletionSourceSpan sourceSpan) {

    public SqlCompletionLocalVariable {
        name = Objects.toString(name, "").trim();
        dataType = blankToNull(dataType);
        sourceType = SqlCompletionLocalSymbolSourceTypeEnum.from(sourceType,
                SqlCompletionLocalSymbolSourceTypeEnum.USER_VARIABLE).name();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
