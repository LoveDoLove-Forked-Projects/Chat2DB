package ai.chat2db.community.domain.api.model.completion;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatementWindowTypeEnum;
import java.util.Objects;


public record SqlCompletionStatementWindow(String sourceSql,
                                           String parseSql,
                                           int sourceStartOffset,
                                           int sourceEndOffset,
                                           int cursor,
                                           String type) {

    public SqlCompletionStatementWindow {
        sourceSql = Objects.toString(sourceSql, "");
        parseSql = Objects.toString(parseSql, "");
        if (parseSql.length() != sourceSql.length()) {
            parseSql = sourceSql;
        }
        sourceStartOffset = Math.max(0, sourceStartOffset);
        sourceEndOffset = Math.max(sourceStartOffset, Math.min(sourceEndOffset, sourceStartOffset + sourceSql.length()));
        cursor = Math.max(0, Math.min(cursor, sourceSql.length()));
        type = SqlCompletionStatementWindowTypeEnum.from(type).name();
    }

    public int sourceCursor() {
        return sourceStartOffset + cursor;
    }

    public String sourceBeforeCursor() {
        return sourceSql.substring(0, cursor);
    }

    public String parseBeforeCursor() {
        return parseSql.substring(0, cursor);
    }

    public boolean empty() {
        return sourceSql.isBlank();
    }
}
