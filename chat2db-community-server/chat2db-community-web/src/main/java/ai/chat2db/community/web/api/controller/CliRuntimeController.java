package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.web.api.config.cli.support.CliControllerSupport;

import ai.chat2db.community.domain.api.model.cli.CliRuntimeCapabilities;
import ai.chat2db.community.domain.api.model.cli.CliRuntimeHealth;
import ai.chat2db.community.domain.api.service.sys.ISysApplicationLifecycleService;
import ai.chat2db.community.domain.api.service.cli.ICliRuntimeService;
import ai.chat2db.community.web.api.config.cli.security.CliRuntimeOnly;
import ai.chat2db.community.web.api.model.response.cli.CliResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes CLI runtime health and capability endpoints.
 */
@RestController
@CliRuntimeOnly
@RequestMapping("/api/cli/v1/runtime")
public class CliRuntimeController extends CliControllerSupport {

    private final ICliRuntimeService cliRuntimeService;
    private final ISysApplicationLifecycleService applicationLifecycleService;

    public CliRuntimeController(ICliRuntimeService cliRuntimeService,
                                ISysApplicationLifecycleService applicationLifecycleService) {
        this.cliRuntimeService = cliRuntimeService;
        this.applicationLifecycleService = applicationLifecycleService;
    }

    /**
     * Returns CLI runtime.
     * <p>
     * Endpoint: {@code GET /api/cli/v1/runtime/health}.
     *
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI runtime health response.
     */
    @GetMapping("/health")
    public CliResult<CliRuntimeHealth> health(HttpServletRequest servletRequest) {
        return CliResult.ok(cliRuntimeService.health(), requestId(servletRequest));
    }

    /**
     * Returns CLI runtime.
     * <p>
     * Endpoint: {@code GET /api/cli/v1/runtime/capabilities}.
     *
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI runtime capabilities response.
     */
    @GetMapping("/capabilities")
    public CliResult<CliRuntimeCapabilities> capabilities(HttpServletRequest servletRequest) {
        return CliResult.ok(cliRuntimeService.capabilities(), requestId(servletRequest));
    }

    /**
     * Handles shutdown for CLI runtime.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/runtime/shutdown}.
     *
     * @param servletRequest HTTP request context.
     * @return controller response containing boolean.
     */
    @PostMapping("/shutdown")
    public CliResult<Boolean> shutdown(HttpServletRequest servletRequest) {
        String requestId = requestId(servletRequest);
        return CliResult.ok(applicationLifecycleService.shutdownCliRuntime(), requestId);
    }
}
