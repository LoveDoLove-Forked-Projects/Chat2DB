package ai.chat2db.community.domain.api.model.completion.context;

import java.util.List;

public record SqlCompletionLocalContext(List<SqlCompletionLocalRelation> relations,
                                        List<SqlCompletionLocalVariable> variables) {

    private static final SqlCompletionLocalContext EMPTY = new SqlCompletionLocalContext(List.of(), List.of());

    public SqlCompletionLocalContext {
        relations = relations == null ? List.of() : List.copyOf(relations);
        variables = variables == null ? List.of() : List.copyOf(variables);
    }

    public static SqlCompletionLocalContext empty() {
        return EMPTY;
    }

    public boolean emptyContext() {
        return relations.isEmpty() && variables.isEmpty();
    }
}
