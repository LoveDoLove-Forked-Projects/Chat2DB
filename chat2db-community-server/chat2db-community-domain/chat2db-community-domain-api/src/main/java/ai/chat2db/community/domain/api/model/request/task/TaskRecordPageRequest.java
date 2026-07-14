package ai.chat2db.community.domain.api.model.request.task;

import ai.chat2db.community.tools.wrapper.param.PageQueryParam;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRecordPageRequest  extends PageQueryParam {

    @Size(max = 64)
    private String taskStatus;

}
