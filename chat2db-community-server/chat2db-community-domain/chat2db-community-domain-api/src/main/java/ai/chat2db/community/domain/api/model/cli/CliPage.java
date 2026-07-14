package ai.chat2db.community.domain.api.model.cli;

import java.util.List;

import lombok.Data;

@Data
public class CliPage<T> {

    private List<T> items;

    private Integer pageNo;

    private Integer pageSize;

    private Long total;

    public static <T> CliPage<T> of(List<T> items, Integer pageNo, Integer pageSize, Long total) {
        CliPage<T> page = new CliPage<>();
        page.setItems(items);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setTotal(total);
        return page;
    }
}
