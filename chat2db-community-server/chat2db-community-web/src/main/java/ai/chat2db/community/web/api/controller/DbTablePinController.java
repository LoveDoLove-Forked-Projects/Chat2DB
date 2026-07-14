package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.model.pin.PinTable;
import ai.chat2db.community.domain.api.model.request.pin.DbTablePinRequest;
import ai.chat2db.community.domain.api.service.db.IDbTablePinService;
import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.wrapper.result.ListResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Manages pinned resources in the web client.
 */
@RequestMapping("/api/pin")
@RestController
public class DbTablePinController {

    private final IDbTablePinService tablePinService;

    public DbTablePinController(IDbTablePinService tablePinService) {
        this.tablePinService = tablePinService;
    }

    /**
     * Handles add for pinned resources.
     * <p>
     * Endpoint: {@code POST /api/pin/table/add}.
     *
     * @param request request payload or query parameters for the operation.
     * @return operation result for the request.
     */
    @PostMapping("/table/add")
    public ActionResult add(@Valid @RequestBody PinTable request) {
        tablePinService.pinTable(request);
        return ActionResult.isSuccess();
    }

    /**
     * Deletes pinned resources.
     * <p>
     * Endpoint: {@code POST /api/pin/table/delete}.
     *
     * @param request request payload or query parameters for the operation.
     * @return operation result for the request.
     */
    @PostMapping("/table/delete")
    public ActionResult delete(@Valid @RequestBody PinTable request) {
        tablePinService.deletePinTable(request);
        return ActionResult.isSuccess();
    }

    /**
     * Lists pinned resources.
     * <p>
     * Endpoint: {@code GET /api/pin/table/list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing string.
     */
    @GetMapping("/table/list")
    public ListResult<String> list(DbTablePinRequest request) {
        return ListResult.of(tablePinService.queryPinTables(request));
    }

}
