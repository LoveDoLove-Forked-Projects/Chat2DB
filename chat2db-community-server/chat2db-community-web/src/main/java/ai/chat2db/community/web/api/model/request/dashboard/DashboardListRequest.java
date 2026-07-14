package ai.chat2db.community.web.api.model.request.dashboard;

import lombok.Data;

@Data
public class DashboardListRequest {

    private Integer pageNo = 1;

    private Integer pageSize = 20;

    private String searchKey;

    public Integer getPageNoOrDefault() {
        return pageNo == null ? 1 : pageNo;
    }

    public Integer getPageSizeOrDefault() {
        return pageSize == null ? 20 : pageSize;
    }
}
