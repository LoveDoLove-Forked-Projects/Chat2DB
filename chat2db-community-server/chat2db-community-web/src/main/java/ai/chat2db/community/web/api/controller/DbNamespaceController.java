package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.model.storage.WorkspaceDataSourceNamespace;
import ai.chat2db.community.domain.api.model.workspace.Namespace;
import ai.chat2db.community.domain.api.model.workspace.Node;
import ai.chat2db.community.domain.api.service.db.IDbNamespaceService;
import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.tools.wrapper.result.ListResult;
import ai.chat2db.community.web.api.converter.data.source.DataSourceWebConverter;
import ai.chat2db.community.web.api.model.request.data.source.BaseRefreshParam;
import ai.chat2db.community.web.api.model.request.data.source.PositionUpdateRequest;
import ai.chat2db.community.web.api.model.request.data.source.UpdateDatasourcePositionRequest;
import ai.chat2db.community.web.api.model.response.data.source.DataSourceNamespaceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages datasource namespace trees and ordering endpoints.
 */
@RequestMapping("/api/namespaces")
@RestController
@Slf4j
public class DbNamespaceController {

    private final IDbNamespaceService namespaceService;
    private final DataSourceWebConverter dataSourceWebConverter;

    public DbNamespaceController(IDbNamespaceService namespaceService,
            DataSourceWebConverter dataSourceWebConverter) {
        this.namespaceService = namespaceService;
        this.dataSourceWebConverter = dataSourceWebConverter;
    }

    /**
     * Lists data source.
     * <p>
     * Endpoint: {@code GET /api/namespaces/data_source_list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing data source namespace response.
     */
    @GetMapping("/data_source_list")
    public DataResult<DataSourceNamespaceResponse> listDataSource(BaseRefreshParam request) {
        WorkspaceDataSourceNamespace result = namespaceService.getNamespaceDataSources(request.isRefresh());
        return DataResult.of(result == null ? null : dataSourceWebConverter.storage2response(result));
    }

    /**
     * Creates namespaces.
     * <p>
     * Endpoint: {@code POST /api/namespaces/create}.
     *
     * @param namespace namespace value.
     * @return data result containing long.
     */
    @PostMapping("/create")
    public DataResult<Long> create(@RequestBody Namespace namespace) {
        return DataResult.of(namespaceService.createNamespace(namespace));
    }

    /**
     * Updates namespaces.
     * <p>
     * Endpoint: {@code POST /api/namespaces/update}.
     *
     * @param namespace namespace value.
     * @return operation result for the request.
     */
    @PostMapping("/update")
    public ActionResult update(@RequestBody Namespace namespace) {
        namespaceService.updateNamespace(namespace);
        return ActionResult.isSuccess();
    }

    /**
     * Deletes namespaces.
     * <p>
     * Endpoint: {@code POST /api/namespaces/delete}.
     *
     * @param body body value.
     * @return operation result for the request.
     */
    @PostMapping("/delete")
    public ActionResult delete(@RequestBody Namespace body) {
        namespaceService.deleteNamespace(body.getId());
        return ActionResult.isSuccess();
    }

    /**
     * Updates data source position.
     * <p>
     * Endpoint: {@code POST /api/namespaces/update_data_source_position}.
     *
     * @param request request payload or query parameters for the operation.
     * @return operation result for the request.
     */
    @PostMapping("/update_data_source_position")
    public ActionResult updateDataSourcePosition(@RequestBody UpdateDatasourcePositionRequest request) {
        namespaceService.updateDataSourcePosition(dataSourceWebConverter.request2param(request));
        return ActionResult.isSuccess();
    }

    /**
     * Handles tree for namespaces.
     * <p>
     * Endpoint: {@code GET /api/namespaces/tree_list}.
     *
     * @return list result for the request.
     */
    @GetMapping("/tree_list")
    public ListResult<Node> tree() {
        List<Node> nodes = namespaceService.getTree();
        return ListResult.of(nodes);
    }

    /**
     * Updates position.
     * <p>
     * Endpoint: {@code POST /api/namespaces/update_position}.
     *
     * @param request request payload or query parameters for the operation.
     * @return operation result for the request.
     */
    @PostMapping("/update_position")
    public ActionResult updatePosition(@RequestBody PositionUpdateRequest request) {
        namespaceService.updatePositionIfChanged(request.getDropToNode(), request.getDragNode(), request.getDropPosition());
        return ActionResult.isSuccess();
    }

}
