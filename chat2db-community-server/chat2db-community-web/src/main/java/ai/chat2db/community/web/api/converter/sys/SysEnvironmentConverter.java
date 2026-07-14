package ai.chat2db.community.web.api.converter.sys;

import ai.chat2db.community.domain.api.config.Environment;
import ai.chat2db.community.web.api.model.response.environment.SimpleEnvironmentResponse;
import org.springframework.stereotype.Component;

@Component
public class SysEnvironmentConverter {

    public SimpleEnvironmentResponse toResponse(Environment environment) {
        if (environment == null) {
            return null;
        }
        return SimpleEnvironmentResponse.builder()
                .id(environment.getId())
                .name(environment.getName())
                .shortName(environment.getShortName())
                .color(environment.getColor())
                .build();
    }
}
