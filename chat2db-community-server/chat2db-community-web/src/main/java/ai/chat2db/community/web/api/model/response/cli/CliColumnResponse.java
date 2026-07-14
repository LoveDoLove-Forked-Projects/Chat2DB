package ai.chat2db.community.web.api.model.response.cli;

import lombok.Data;

@Data
public class CliColumnResponse {

    private String name;

    private String tableName;

    private String columnType;

    private Integer dataType;

    private String defaultValue;

    private Boolean autoIncrement;

    private String comment;

    private Boolean primaryKey;

    private String schemaName;

    private String databaseName;

    private Integer columnSize;

    private Integer decimalDigits;

    private Integer ordinalPosition;

    private Integer nullable;
}
