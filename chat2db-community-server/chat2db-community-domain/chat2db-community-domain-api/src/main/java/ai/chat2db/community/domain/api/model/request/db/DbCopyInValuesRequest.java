package ai.chat2db.community.domain.api.model.request.db;

import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.ResultOperation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DbCopyInValuesRequest {

    public static final String SOURCE_TYPE_RESULT_SET = "RESULT_SET";

    public static final String SOURCE_TYPE_EXTERNAL_TEXT = "EXTERNAL_TEXT";

    @NotNull
    private Long dataSourceId;

    private Long consoleId;

    private String databaseName;

    private String schemaName;

    private List<Header> headerList;

    private List<ResultOperation> operations;

    private List<String> externalValues;

    private String sourceType;
}
