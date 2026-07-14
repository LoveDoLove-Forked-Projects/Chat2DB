package ai.chat2db.community.domain.api.model.completion;


public record SqlCompletionMetadataScope(String catalog,
                                         String schema,
                                         String table,
                                         String object) {

    public static SqlCompletionMetadataScope empty() {
        return new SqlCompletionMetadataScope(null, null, null, null);
    }
}
