package ai.chat2db.community.web.api.config.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.BodyFilter;


@Configuration
public class WebLogConfiguration {

    @Bean
    public BodyFilter bodyFilter() {
        return BodyFilter.none();
    }
}
