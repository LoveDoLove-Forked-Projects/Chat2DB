package ai.chat2db.community.domain.api.model.completion.result;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionEditorHint;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;


@Data
public class SqlCompletionResponse {

    private String status = SqlCompletionStatusEnum.EMPTY.name();
    private int replaceStart;
    private int replaceEnd;
    private List<SqlCompletionCandidate> candidates = new ArrayList<>();
    private List<SqlCompletionEditorHint> editorHints = new ArrayList<>();
    private String reasonCode;
    private SqlCompletionTrace trace = SqlCompletionTrace.empty();

    public static SqlCompletionResponse success(int replaceStart,
                                              int replaceEnd,
                                              List<SqlCompletionCandidate> candidates) {
        SqlCompletionResponse result = new SqlCompletionResponse();
        result.setStatus(SqlCompletionStatusEnum.SUCCESS.name());
        result.setReplaceStart(Math.max(0, replaceStart));
        result.setReplaceEnd(Math.max(result.getReplaceStart(), replaceEnd));
        result.setCandidates(candidates == null ? new ArrayList<>() : candidates);
        return result;
    }

    public static SqlCompletionResponse empty() {
        SqlCompletionResponse result = new SqlCompletionResponse();
        result.setStatus(SqlCompletionStatusEnum.EMPTY.name());
        return result;
    }

    public static SqlCompletionResponse unsupported(String databaseType) {
        SqlCompletionResponse result = new SqlCompletionResponse();
        result.setStatus(SqlCompletionStatusEnum.UNSUPPORTED.name());
        result.setReasonCode(databaseType == null || databaseType.isBlank()
                ? "sql.completion.unsupported"
                : "sql.completion.unsupported." + databaseType);
        return result;
    }

    public static SqlCompletionResponse rejected(String reasonCode) {
        SqlCompletionResponse result = new SqlCompletionResponse();
        result.setStatus(SqlCompletionStatusEnum.REJECTED.name());
        result.setReasonCode(reasonCode);
        return result;
    }
}
