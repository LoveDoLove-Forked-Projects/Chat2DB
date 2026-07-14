package ai.chat2db.community.web.api.converter.data.source;

import ai.chat2db.community.domain.api.model.datasource.SSHInfo;
import ai.chat2db.community.web.api.model.request.data.source.SSHTestRequest;

import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public abstract class SSHWebConverter {


    public abstract SSHInfo toInfo(SSHTestRequest request);
}
