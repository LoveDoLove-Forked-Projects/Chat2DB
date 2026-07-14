package ai.chat2db.community.web.api.model.response.cli;

import java.util.List;

import lombok.Data;

@Data
public class CliPageResponse<T> {

    private List<T> items;

    private Integer pageNo;

    private Integer pageSize;

    private Long total;

    public static <T> CliPageResponse<T> of(List<T> items, Integer pageNo, Integer pageSize, Long total) {
        CliPageResponse<T> page = new CliPageResponse<>();
        page.setItems(items);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setTotal(total);
        return page;
    }
}
