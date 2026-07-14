package ai.chat2db.community.web.api.converter.er;

import ai.chat2db.community.domain.api.model.er.ERPosition;
import ai.chat2db.community.domain.api.model.request.er.DbErQueryRequest;
import ai.chat2db.community.web.api.model.request.er.ERModelPositionSaveRequest;
import ai.chat2db.community.web.api.model.request.er.ERModelQueryRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ErWebConverter {

    public abstract DbErQueryRequest request2param(ERModelQueryRequest request);

    public abstract ERPosition request2position(ERModelPositionSaveRequest request);
}
