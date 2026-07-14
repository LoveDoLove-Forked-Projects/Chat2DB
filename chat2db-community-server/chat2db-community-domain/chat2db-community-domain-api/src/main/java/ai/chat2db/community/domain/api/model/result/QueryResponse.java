package ai.chat2db.community.domain.api.model.result;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class QueryResponse {

    private String tableName;
    private List<Header> headerList;
    private List<ResultOperation> operations;
    private Map<String, Object> extra;
}
