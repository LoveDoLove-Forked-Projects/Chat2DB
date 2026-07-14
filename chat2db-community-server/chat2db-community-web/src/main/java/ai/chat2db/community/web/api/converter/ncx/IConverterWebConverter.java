package ai.chat2db.community.web.api.converter.ncx;

import ai.chat2db.community.domain.api.model.ncx.NcxImportResponse;
import ai.chat2db.community.web.api.model.response.ncx.UploadResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IConverterWebConverter {

    UploadResponse ncxImportResult2response(NcxImportResponse result);
}
