package ai.chat2db.community.domain.api.model;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class PageResponse<T> {

    private List<T> data;

    private Integer pageNo;

    private Integer pageSize;

    private Long total;

    private Boolean hasNextPage;

    public PageResponse() {
        this.data = Collections.emptyList();
        this.pageNo = 1;
        this.pageSize = 10;
        this.total = 0L;
    }

    public static <T> PageResponse<T> of(List<T> data, Long total, Integer pageNo, Integer pageSize) {
        PageResponse<T> page = new PageResponse<>();
        page.setData(data == null ? Collections.emptyList() : data);
        page.setTotal(total == null ? 0L : total);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setHasNextPage(page.calculateHasNextPage());
        return page;
    }

    public static <T> PageResponse<T> empty(Integer pageNo, Integer pageSize) {
        return of(Collections.emptyList(), 0L, pageNo, pageSize);
    }

    public <R> PageResponse<R> map(Function<T, R> mapper) {
        List<R> mappedData = data == null ? Collections.emptyList() : data.stream().map(mapper).collect(Collectors.toList());
        return PageResponse.of(mappedData, total, pageNo, pageSize);
    }

    private Boolean calculateHasNextPage() {
        if (total != null && total > 0 && pageNo != null && pageSize != null) {
            return (long) pageSize * pageNo < total;
        }
        return data != null && pageSize != null && data.size() >= pageSize;
    }
}
