package ai.chat2db.community.web.api.model.request.cli;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CliTablesListRequest extends CliConnectionRequest {

    private String searchKey;
}
