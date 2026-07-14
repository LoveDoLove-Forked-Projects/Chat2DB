package ai.chat2db.community.domain.api.model.cli;

import java.util.List;

import lombok.Data;

@Data
public class CliSqlQueryResponse {

    private List<CliSqlColumn> columns;

    private List<List<String>> rows;

    private Integer rowCount;

    private Integer updateCount;

    private Boolean hasNextPage;

    private Boolean truncated;

    private Long duration;

    private Integer pageNo;

    private Integer pageSize;

    private String resultSetId;
}
