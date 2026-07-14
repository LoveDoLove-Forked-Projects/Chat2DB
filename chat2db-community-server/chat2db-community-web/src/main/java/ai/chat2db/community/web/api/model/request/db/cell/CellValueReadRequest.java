package ai.chat2db.community.web.api.model.request.db.cell;

import lombok.Data;

@Data
public class CellValueReadRequest {
    private String largeValueId;
    private Long offset;
    private Integer limit;
    private String format;
}
