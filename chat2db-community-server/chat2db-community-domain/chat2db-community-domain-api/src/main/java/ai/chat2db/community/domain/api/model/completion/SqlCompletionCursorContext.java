package ai.chat2db.community.domain.api.model.completion;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;


public record SqlCompletionCursorContext(boolean admitted,
                                         String rejectReason,
                                         SqlCompletionMetadataScope scope,
                                         String prefix,
                                         int replaceStart,
                                         int replaceEnd,
                                         boolean dotScoped) {

    public SqlCompletionCursorContext {
        rejectReason = Objects.toString(rejectReason, "");
        scope = Objects.isNull(scope) ? SqlCompletionMetadataScope.empty() : scope;
        prefix = Objects.toString(prefix, "");
        replaceStart = Math.max(0, replaceStart);
        replaceEnd = Math.max(replaceStart, replaceEnd);
    }

    public boolean rejected() {
        return !admitted;
    }

    public boolean hasScopedObject() {
        return StringUtils.isNotBlank(scope.table()) || StringUtils.isNotBlank(scope.object());
    }

    public static SqlCompletionCursorContext empty(int cursor) {
        int safeCursor = Math.max(0, cursor);
        return rejected("NOT_TIP_CURSOR", safeCursor);
    }

    public static SqlCompletionCursorContext rejected(String reason, int cursor) {
        int safeCursor = Math.max(0, cursor);
        return new SqlCompletionCursorContext(false, reason, SqlCompletionMetadataScope.empty(), "", safeCursor,
                safeCursor, false);
    }

    public static SqlCompletionCursorContext admitted(SqlCompletionMetadataScope scope,
                                                      String prefix,
                                                      int replaceStart,
                                                      int replaceEnd,
                                                      boolean dotScoped) {
        return new SqlCompletionCursorContext(true, "", scope, prefix, replaceStart, replaceEnd, dotScoped);
    }
}
