package ai.chat2db.community.domain.api.model.request.db;

import ai.chat2db.community.domain.api.model.db.LargeValueReference;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbCellValueChunkReadRequest {

    @NotNull
    private LargeValueReference reference;

    private Long offset;

    private Integer limit;

    private String format;
}
