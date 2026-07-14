package ai.chat2db.community.domain.api.model.completion.context;

public record SqlCompletionSourceSpan(int startOffset,
                                      int endOffset) {

    public SqlCompletionSourceSpan {
        startOffset = Math.max(0, startOffset);
        endOffset = Math.max(startOffset, endOffset);
    }

    public static SqlCompletionSourceSpan of(int startOffset, int endOffset) {
        return new SqlCompletionSourceSpan(startOffset, endOffset);
    }
}
