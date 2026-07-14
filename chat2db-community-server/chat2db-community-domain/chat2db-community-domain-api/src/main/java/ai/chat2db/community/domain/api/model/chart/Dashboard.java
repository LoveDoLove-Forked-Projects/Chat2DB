package ai.chat2db.community.domain.api.model.chart;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Dashboard {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String name;

    private String description;

    private Long dataSourceCollectionId;

    private List<Long> chartIds;

    private String schema;

    private String refreshType;

    private Object refreshCycle;

    private Long userId;
}
