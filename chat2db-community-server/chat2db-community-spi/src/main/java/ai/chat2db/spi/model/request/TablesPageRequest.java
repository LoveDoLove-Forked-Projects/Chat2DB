package ai.chat2db.spi.model.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TablesPageRequest {

    private String databaseName;

    private String schemaName;

    private String tableNamePattern;

    @Min(1)
    private int pageNo;

    @Min(1)
    private int pageSize;
}
