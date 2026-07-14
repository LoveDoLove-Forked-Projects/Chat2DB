package ai.chat2db.community.domain.api.model.request.db;

import jakarta.validation.Valid;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DbTableDdlRequest extends DbTableQueryRequest {


    private Map<String, String> columnCommentAlias;


    private Map<String, String> columnAlias;


    private String tableCommentAlias;


    private String tableAlias;


    @Valid
    private List<DbColumnCommentUpdateRequest> updateRequestColumnAlias;

}
