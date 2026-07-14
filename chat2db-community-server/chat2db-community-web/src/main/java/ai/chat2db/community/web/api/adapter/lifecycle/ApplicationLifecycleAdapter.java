package ai.chat2db.community.web.api.adapter.lifecycle;

import ai.chat2db.community.domain.api.service.sys.ISysApplicationLifecycleService;
import ai.chat2db.community.tools.model.ConfigJson;
import ai.chat2db.community.tools.util.ConfigUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLifecycleAdapter implements ISysApplicationLifecycleService {

    private final ApplicationContext applicationContext;

    public ApplicationLifecycleAdapter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String getSystemUuid() {
        ConfigJson configJson = ConfigUtils.getConfig();
        return configJson == null ? null : configJson.getSystemUuid();
    }

    @Override
    public boolean shutdownCliRuntime() {
        Thread thread = new Thread(() -> {
            sleep(200L);
            int exitCode = SpringApplication.exit(applicationContext, () -> 0);
            System.exit(exitCode);
        }, "chat2db-cli-runtime-shutdown");
        thread.setDaemon(false);
        thread.start();
        return true;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
