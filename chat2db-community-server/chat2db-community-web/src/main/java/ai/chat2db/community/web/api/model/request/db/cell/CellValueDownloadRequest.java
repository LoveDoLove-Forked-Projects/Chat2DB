package ai.chat2db.community.web.api.model.request.db.cell;

import lombok.Data;

@Data
public class CellValueDownloadRequest {
    private String largeValueId;
    private String format;
}
