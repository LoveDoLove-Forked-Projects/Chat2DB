package ai.chat2db.community.web.api.config.listener;

import ai.chat2db.community.web.api.config.console.ConsoleHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy(value = false)
public class AppStartListener implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        ConsoleHelper.init();
    }
}
