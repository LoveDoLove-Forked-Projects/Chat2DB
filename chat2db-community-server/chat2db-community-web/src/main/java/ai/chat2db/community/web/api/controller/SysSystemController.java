package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.service.sys.ISysApplicationLifecycleService;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.web.api.model.response.system.SystemResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Exposes system metadata for desktop service readiness checks.
 */
@RestController
@RequestMapping("/api/system")
public class SysSystemController {

    private final ISysApplicationLifecycleService applicationLifecycleService;

    public SysSystemController(ISysApplicationLifecycleService applicationLifecycleService) {
        this.applicationLifecycleService = applicationLifecycleService;
    }

    /**
     * Gets system state.
     * <p>
     * Endpoint: {@code GET /api/system}.
     *
     * @return data result containing system response.
     */
    @GetMapping
    public DataResult<SystemResponse> get() {
        return DataResult.of(SystemResponse.builder()
                .systemUuid(applicationLifecycleService.getSystemUuid())
                .build());
    }
}
