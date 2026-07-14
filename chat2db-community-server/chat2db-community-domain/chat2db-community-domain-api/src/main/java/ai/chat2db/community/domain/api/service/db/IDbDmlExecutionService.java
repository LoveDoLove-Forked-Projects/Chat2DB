package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.request.db.DbDmlExecutionRequest;
import ai.chat2db.community.domain.api.model.request.db.DbSelectResultUpdateRequest;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;

import java.util.List;

public interface IDbDmlExecutionService {

    List<ExecuteResponse> execute(DbDmlExecutionRequest request);

    List<ExecuteResponse> executeTable(DbDmlExecutionRequest request);

    ExecuteResponse executeUpdate(DbDmlExecutionRequest request);

    ExecuteResponse executeDdl(DbDmlExecutionRequest request);

    void rejectPartialLargeValueOperations(DbSelectResultUpdateRequest request);
}
