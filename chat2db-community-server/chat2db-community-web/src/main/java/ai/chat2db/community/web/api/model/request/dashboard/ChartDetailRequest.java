package ai.chat2db.community.web.api.model.request.dashboard;

import lombok.Data;

@Data
public class ChartDetailRequest {

    private Long chartId;

    private Boolean refresh = Boolean.FALSE;
}
