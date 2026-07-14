package ai.chat2db.community.domain.api.model.request.db;

import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.ResultOperation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DbSelectResultUpdateRequest {


    @NotNull
    private Long consoleId;


    @NotNull
    private Long dataSourceId;


    private String databaseName;


    private String schemaName;


    @NotEmpty
    private List<Header> headerList;


    @NotEmpty
    private List<ResultOperation> operations;


    @NotEmpty
    private String tableName;


    private Map<String,Object> extra;
}
