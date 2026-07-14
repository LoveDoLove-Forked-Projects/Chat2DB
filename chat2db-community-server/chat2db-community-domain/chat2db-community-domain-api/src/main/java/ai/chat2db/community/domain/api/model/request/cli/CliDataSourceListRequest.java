package ai.chat2db.community.domain.api.model.request.cli;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CliDataSourceListRequest extends CliPageQueryRequest {

    @Size(max = 256)
    private String searchKey;
}
