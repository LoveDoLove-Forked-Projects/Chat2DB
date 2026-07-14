package ai.chat2db.community.web.api.model.response.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IndexColumnResponse {


    private String indexName;


    private String tableName;


    private String type;


    private String comment;


    private String columnName;


    private Short ordinalPosition;


    private String collation;


    private String schemaName;


    private String databaseName;


    private Boolean nonUnique;


    private String indexQualifier;


    private String ascOrDesc;


    private Long cardinality;


    private Long pages;


    private String filterCondition;
}

