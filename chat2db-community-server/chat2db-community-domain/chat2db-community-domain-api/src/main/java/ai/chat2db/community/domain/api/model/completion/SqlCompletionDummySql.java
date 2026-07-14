package ai.chat2db.community.domain.api.model.completion;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionDummyTypeEnum;
import java.util.Objects;


public record SqlCompletionDummySql(String sql,
                                    int cursor,
                                    String type,
                                    int insertedOffset,
                                    int insertedLength) {

    public SqlCompletionDummySql {
        sql = Objects.toString(sql, "");
        cursor = Math.max(0, Math.min(cursor, sql.length()));
        type = SqlCompletionDummyTypeEnum.from(type).name();
        insertedOffset = Math.max(0, Math.min(insertedOffset, sql.length()));
        insertedLength = Math.max(0, insertedLength);
    }

    public static SqlCompletionDummySql unchanged(String sql, int cursor) {
        return new SqlCompletionDummySql(sql, cursor, SqlCompletionDummyTypeEnum.NONE.name(), cursor, 0);
    }
}
