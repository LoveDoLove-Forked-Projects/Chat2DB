package ai.chat2db.community.web.api.config.core;

import ai.chat2db.community.domain.api.service.db.IDbJdbcDriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class JarDownloadTask implements CommandLineRunner {

    private final IDbJdbcDriverService jdbcDriverService;

    public JarDownloadTask(IDbJdbcDriverService jdbcDriverService) {
        this.jdbcDriverService = jdbcDriverService;
    }

    @Override
    public void run(String... args) throws Exception {
        jdbcDriverService.downloadStartupDrivers();
    }
}
