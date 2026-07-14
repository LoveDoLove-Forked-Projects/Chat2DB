package ai.chat2db.spi.model.response;

import ai.chat2db.community.domain.api.model.metadata.Table;
import java.util.Collections;
import java.util.List;
import lombok.Data;

@Data
public class TablesPageResponse {

    private List<Table> data;

    private Integer pageNo;

    private Integer pageSize;

    private Long total;

    private Boolean hasNextPage;

    public TablesPageResponse() {
        this.data = Collections.emptyList();
        this.pageNo = 1;
        this.pageSize = 10;
        this.total = 0L;
    }

    public static TablesPageResponse of(List<Table> data, Long total, Integer pageNo, Integer pageSize) {
        TablesPageResponse response = new TablesPageResponse();
        response.setData(data == null ? Collections.emptyList() : data);
        response.setTotal(total == null ? 0L : total);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setHasNextPage(response.calculateHasNextPage());
        return response;
    }

    private Boolean calculateHasNextPage() {
        if (total != null && total > 0 && pageNo != null && pageSize != null) {
            return (long) pageSize * pageNo < total;
        }
        return data != null && pageSize != null && data.size() >= pageSize;
    }
}
