package ai.chat2db.community.domain.api.model.completion.context;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionLocalSymbolSourceTypeEnum;
import java.util.List;
import java.util.Objects;

public record SqlCompletionLocalRelation(String catalog,
                                         String schema,
                                         String name,
                                         String alias,
                                         List<SqlCompletionLocalColumn> columns,
                                         String sourceType,
                                         SqlCompletionSourceSpan sourceSpan) {

    public SqlCompletionLocalRelation {
        catalog = blankToNull(catalog);
        schema = blankToNull(schema);
        name = Objects.toString(name, "").trim();
        alias = blankToNull(alias);
        columns = columns == null ? List.of() : List.copyOf(columns);
        sourceType = SqlCompletionLocalSymbolSourceTypeEnum.from(sourceType,
                SqlCompletionLocalSymbolSourceTypeEnum.CURRENT_STATEMENT).name();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
