package ai.chat2db.community.web.api.config.listener;

import ai.chat2db.community.domain.api.service.db.IDbDataSourceService;
import ai.chat2db.community.web.api.util.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextClosedListener implements ApplicationListener<ContextClosedEvent> {

    private final IDbDataSourceService dataSourceService;

    public ContextClosedListener(IDbDataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            log.info("close context");
            SystemUtils.stop(dataSourceService::closeRuntime);
        }catch (Exception e){
        }
    }
}
