package ai.chat2db.community.domain.api.model.db;

import lombok.Data;

import java.util.List;


@Data
public class SimpleFunction {

    private String datasourceName;
    private String databaseName;
    private String schemaName;
    private String functionName;
    private String insertText;

    private String returnType;
    private List<SimpleParameter> parameters;
}
