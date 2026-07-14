package ai.chat2db.community.domain.api.model.completion.result;

import java.util.Objects;


public record SqlCompletionInputCleanResponse(String sourceSql,
                                            String parseSql,
                                            int cursor) {

    public SqlCompletionInputCleanResponse {
        sourceSql = Objects.toString(sourceSql, "");
        parseSql = Objects.toString(parseSql, "");
        if (parseSql.length() != sourceSql.length()) {
            parseSql = sourceSql;
        }
        cursor = Math.max(0, Math.min(cursor, sourceSql.length()));
    }
}
