package ai.chat2db.community.web.api.model.request.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableSchemaRequest {

    private Long organizationId;

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String tableComment;

    private String tableCommentExt;

    private String tableSchemaContent;

    private String searchKey;

    private Integer size = 10;

    private Boolean addToCollection = false;




    private String type;
}
