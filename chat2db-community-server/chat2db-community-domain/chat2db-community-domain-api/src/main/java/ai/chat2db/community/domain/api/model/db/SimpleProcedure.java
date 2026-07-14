package ai.chat2db.community.domain.api.model.db;

import lombok.Data;

import java.util.List;


@Data
public class SimpleProcedure {
    private String datasourceName;
    private String databaseName;
    private String schemaName;
    private String procedureName;
    private String insertText;

    private List<SimpleParameter> parameters;
    private String returnType;
}
