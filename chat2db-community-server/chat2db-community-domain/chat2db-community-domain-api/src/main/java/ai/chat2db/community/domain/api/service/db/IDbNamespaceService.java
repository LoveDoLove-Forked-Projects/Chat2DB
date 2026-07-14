package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.request.datasource.DbDataSourcePositionUpdateRequest;
import ai.chat2db.community.domain.api.model.storage.WorkspaceDataSourceNamespace;
import ai.chat2db.community.domain.api.model.workspace.Namespace;
import ai.chat2db.community.domain.api.model.workspace.Node;

import java.util.List;

/**
 * Manages datasource namespaces and workspace tree ordering.
 */
public interface IDbNamespaceService {

    WorkspaceDataSourceNamespace getNamespaceDataSources();

    WorkspaceDataSourceNamespace getNamespaceDataSources(boolean refresh);

    Long createNamespace(Namespace namespace);

    void updateNamespace(Namespace namespace);

    void deleteNamespace(Long id);

    void updateDataSourcePosition(DbDataSourcePositionUpdateRequest request);

    List<Node> getTree();

    void updatePosition(Node dropToNode, Node dragNode, Integer dropPosition);

    /**
     * Updates workspace tree position and ignores no-op same-node moves.
     *
     * @param dropToNode target node.
     * @param dragNode node being moved.
     * @param dropPosition target drop position.
     */
    void updatePositionIfChanged(Node dropToNode, Node dragNode, Integer dropPosition);
}
