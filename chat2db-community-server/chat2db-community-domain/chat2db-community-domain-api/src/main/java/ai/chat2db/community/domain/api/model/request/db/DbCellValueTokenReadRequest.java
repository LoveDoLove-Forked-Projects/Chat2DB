package ai.chat2db.community.domain.api.model.request.db;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DbCellValueTokenReadRequest {

    @NotBlank
    private String largeValueId;

    private Long offset;

    private Integer limit;

    private String format;
}
