package ai.chat2db.community.domain.api.model.request.cli;

import lombok.Data;

@Data
public class CliPageQueryRequest {

    private Integer pageNo = 1;

    private Integer pageSize = 100;

    private Boolean refresh = Boolean.FALSE;

    public Integer safePageNo() {
        return pageNo == null || pageNo < 1 ? 1 : pageNo;
    }

    public Integer safePageSize() {
        if (pageSize == null || pageSize < 1) {
            return 100;
        }
        return Math.min(pageSize, 1000);
    }

    public boolean refresh() {
        return Boolean.TRUE.equals(refresh);
    }
}
