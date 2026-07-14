package ai.chat2db.community.domain.api.model.completion.result;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;


@Data
public class SqlCompletionMetadataResponse {

    private String status = SqlCompletionStatusEnum.EMPTY.name();
    private List<SqlCompletionCandidate> candidates = new ArrayList<>();
    private String reasonCode;

    public static SqlCompletionMetadataResponse of(List<SqlCompletionCandidate> candidates) {
        SqlCompletionMetadataResponse result = new SqlCompletionMetadataResponse();
        result.setStatus(SqlCompletionStatusEnum.SUCCESS.name());
        result.setCandidates(candidates == null ? new ArrayList<>() : candidates);
        return result;
    }

    public static SqlCompletionMetadataResponse unsupported() {
        SqlCompletionMetadataResponse result = new SqlCompletionMetadataResponse();
        result.setStatus(SqlCompletionStatusEnum.UNSUPPORTED.name());
        result.setReasonCode("sql.completion.metadata.unsupported");
        return result;
    }
}
