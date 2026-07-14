package ai.chat2db.community.domain.api.model.db;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
public class LargeValueReference {

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String columnName;

    @Builder.Default
    private Map<String, Object> primaryKey = new LinkedHashMap<>();

    private String valueType;

    private Integer sqlType;

    private String columnType;

    private Long sizeBytes;

    private Long sizeChars;
}
