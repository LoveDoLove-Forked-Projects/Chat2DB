package ai.chat2db.community.web.api.model.request.cli;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CliDatasourceListRequest extends CliPageRequest {

    private String searchKey;
}
