package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class SchemaCreateRequest extends DataSourceBaseRequest {


    @JsonAlias({"TABLE_SCHEM"})
    private String name;


    private String comment;


    private String owner;
}
