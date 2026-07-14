package ai.chat2db.community.web.api.model.response.db;

import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import lombok.Data;

import java.util.List;
@Data
public class MetaSchemaResponse {


    private List<Database> databases;


    private List<Schema> schemas;
}
