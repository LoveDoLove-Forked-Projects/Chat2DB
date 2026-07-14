package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.model.workspace.Node;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreeNode {
   private List<Node> children;

   private Long id;
}
