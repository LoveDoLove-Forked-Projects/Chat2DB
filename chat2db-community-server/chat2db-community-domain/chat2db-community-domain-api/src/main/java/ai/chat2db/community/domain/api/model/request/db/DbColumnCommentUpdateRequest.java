package ai.chat2db.community.domain.api.model.request.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DbColumnCommentUpdateRequest {


    private String columnName;


    private String columnNameAlias;


    private String columnComment;


    private String columnCommentAlias;


    private String columnExampleData;


    private Map<Object, Object> columnEnumMap;


    private String foreignTableName;


    private String foreignColumnName;


    private String functionExamples;


    private Boolean deletedFlag;


    @Data
    public static class FunctionExample {


        private String functionName;


        private String functionDescription;


        private String functionArgDataTypes;


        private String functionArgDirections;

    }
}
