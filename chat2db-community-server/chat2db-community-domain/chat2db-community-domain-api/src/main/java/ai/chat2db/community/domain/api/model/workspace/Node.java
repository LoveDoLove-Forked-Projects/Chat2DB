package ai.chat2db.community.domain.api.model.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private String type;

    private Long id;

    private Object data;

    private List<Node> children;
}
