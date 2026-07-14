package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.tools.wrapper.result.ListResult;
import ai.chat2db.community.domain.api.service.sys.ISysEnvironmentService;
import ai.chat2db.community.web.api.converter.sys.SysEnvironmentConverter;
import ai.chat2db.community.web.api.model.response.environment.SimpleEnvironmentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes datasource environment endpoints.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class SysEnvironmentController {

    private final ISysEnvironmentService environmentService;
    private final SysEnvironmentConverter sysEnvironmentConverter;

    public SysEnvironmentController(ISysEnvironmentService environmentService,
                                    SysEnvironmentConverter sysEnvironmentConverter) {
        this.environmentService = environmentService;
        this.sysEnvironmentConverter = sysEnvironmentConverter;
    }

    /**
     * Lists datasource environments.
     * <p>
     * Endpoint: {@code GET /api/common/environment/list_all}.
     *
     * @return list result containing simple environment response.
     */
    @GetMapping("/common/environment/list_all")
    public ListResult<SimpleEnvironmentResponse> environmentList() {
        List<SimpleEnvironmentResponse> environments = environmentService.listAll().stream()
                .map(sysEnvironmentConverter::toResponse)
                .toList();
        return ListResult.of(environments);
    }

}
