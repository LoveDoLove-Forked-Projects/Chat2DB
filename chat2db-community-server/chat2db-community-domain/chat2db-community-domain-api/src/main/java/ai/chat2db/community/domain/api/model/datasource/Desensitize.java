package ai.chat2db.community.domain.api.model.datasource;

import lombok.Data;

import java.util.Date;

@Data
public class Desensitize {
    private Long id;




    private Long dataSourceId;


    private String dataSourceName;




    private String databaseName;




    private String schemaName;




    private String tableName;




    private String columnName;




    private String desensitizeType;




    private Date createTime;




    private Date updateTime;

}
