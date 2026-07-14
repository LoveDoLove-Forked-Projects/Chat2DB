package ai.chat2db.community.domain.api.model.chart;

import java.util.Date;

import lombok.Data;


@Data
public class Chart {


    private Long id;


    private Date gmtCreate;


    private Date gmtModified;


    private String name;


    private String description;


    private String schema;


    private Long dataSourceId;


    private String dataSourceName;


    private String schemaName;


    private String type;


    private String databaseName;


    private String ddl;


    private String deleted;


    private Long userId;


    private Object chartSchema;


    private Object metaData;


    private Object databaseInfo;


    private String refreshType;


    private String refreshCycle;
}
