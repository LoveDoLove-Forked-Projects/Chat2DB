package ai.chat2db.community.web.api.converter.operation.log;

import java.util.List;

import org.mapstruct.Mapper;

import ai.chat2db.community.domain.api.model.operation.OperationLog;
import ai.chat2db.community.domain.api.model.request.operation.OpsOperationLogPageQueryRequest;
import ai.chat2db.community.domain.api.model.request.operation.OpsOperationPageQueryRequest;
import ai.chat2db.community.web.api.model.request.operation.log.OperationLogQueryRequest;
import ai.chat2db.community.web.api.model.request.operation.saved.OperationQueryRequest;
import ai.chat2db.community.web.api.model.response.operation.log.OperationLogResponse;

@Mapper(componentModel = "spring")
public abstract class OperationLogConverter {

    public abstract OperationLogResponse toResponse(OperationLog operationLog);




    public abstract List<OperationLogResponse> toResponseList(List<OperationLog> operationLogList);

    public abstract OpsOperationLogPageQueryRequest request2param(OperationLogQueryRequest request);

    public abstract OpsOperationPageQueryRequest request2param(OperationQueryRequest request);

}
