package ai.chat2db.community.web.api.model.request.data.source;

import ai.chat2db.community.domain.api.model.workspace.Node;
import lombok.Data;

@Data
public class PositionUpdateRequest {


    private Node dropToNode;


    private Node dragNode;


    private Integer dropPosition;
}
