package ai.chat2db.community.web.api.config.listener;

import ai.chat2db.community.domain.api.service.db.IDbDataSourceService;
import ai.chat2db.community.web.api.util.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;


@Slf4j
public class FailedEventApplicationListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        log.error("Application startup failed, stopping application", event.getException());
        try {
            IDbDataSourceService dataSourceService = event.getApplicationContext().getBean(IDbDataSourceService.class);
            SystemUtils.stop(dataSourceService::closeRuntime);
        } catch (Exception e) {
            SystemUtils.stop();
        }
    }
}
