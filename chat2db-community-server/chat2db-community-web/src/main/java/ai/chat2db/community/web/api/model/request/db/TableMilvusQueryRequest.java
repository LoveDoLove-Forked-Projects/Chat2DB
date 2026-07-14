package ai.chat2db.community.web.api.model.request.db;


import lombok.Data;

@Data
public class TableMilvusQueryRequest extends TableBriefQueryRequest {

    private String apikey;

    private Long organizationId;

    private String tableName;
}
