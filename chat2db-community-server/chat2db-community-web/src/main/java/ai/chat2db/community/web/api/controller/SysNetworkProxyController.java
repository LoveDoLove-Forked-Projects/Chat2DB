package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.service.sys.ISysNetworkProxyService;
import ai.chat2db.community.tools.network.NetworkProxySettings;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.web.api.model.request.system.NetworkProxyTestRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes network proxy configuration endpoints.
 */
@RestController
@RequestMapping("/api/network/proxy")
public class SysNetworkProxyController {

    private final ISysNetworkProxyService networkProxyService;

    public SysNetworkProxyController(ISysNetworkProxyService networkProxyService) {
        this.networkProxyService = networkProxyService;
    }

    /**
     * Gets network proxy settings.
     * <p>
     * Endpoint: {@code GET /api/network/proxy}.
     *
     * @return data result containing network proxy settings.
     */
    @GetMapping("")
    public DataResult<NetworkProxySettings> get() {
        return DataResult.of(networkProxyService.get());
    }

    /**
     * Saves network proxy settings.
     * <p>
     * Endpoint: {@code POST /api/network/proxy}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing network proxy settings.
     */
    @PostMapping("")
    public DataResult<NetworkProxySettings> save(@RequestBody NetworkProxySettings request) {
        return DataResult.of(networkProxyService.save(request));
    }

    /**
     * Handles test for network proxy settings.
     * <p>
     * Endpoint: {@code POST /api/network/proxy/test}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing boolean.
     * @throws Exception when the operation cannot be completed.
     */
    @PostMapping("/test")
    public DataResult<Boolean> test(@RequestBody NetworkProxyTestRequest request) throws Exception {
        NetworkProxySettings settings = request.getSettings() == null ? networkProxyService.get() : request.getSettings();
        return DataResult.of(networkProxyService.test(settings, request.getTestUrl()));
    }
}
