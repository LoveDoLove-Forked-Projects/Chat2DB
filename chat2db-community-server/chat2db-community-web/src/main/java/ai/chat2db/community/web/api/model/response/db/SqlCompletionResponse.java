package ai.chat2db.community.web.api.model.response.db;

import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionEditorHint;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;


@Data
public class SqlCompletionResponse {

    private String status;
    private int replaceStart;
    private int replaceEnd;
    private List<SqlCompletionCandidate> candidates = new ArrayList<>();
    private List<SqlCompletionEditorHint> editorHints = new ArrayList<>();
    private String reasonCode;

    public static SqlCompletionResponse from(
            ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse result) {
        SqlCompletionResponse vo = new SqlCompletionResponse();
        if (result == null) {
            return vo;
        }
        vo.setStatus(result.getStatus());
        vo.setReplaceStart(result.getReplaceStart());
        vo.setReplaceEnd(result.getReplaceEnd());
        vo.setCandidates(result.getCandidates() == null ? new ArrayList<>() : result.getCandidates());
        vo.setEditorHints(result.getEditorHints() == null ? new ArrayList<>() : result.getEditorHints());
        vo.setReasonCode(result.getReasonCode());
        return vo;
    }
}
