package ai.chat2db.community.web.api.model.request.data.source;

import lombok.Data;

import java.util.List;

@Data
public class DataSourceExportRequest {

    private List<Long> datasourceIds;
}
