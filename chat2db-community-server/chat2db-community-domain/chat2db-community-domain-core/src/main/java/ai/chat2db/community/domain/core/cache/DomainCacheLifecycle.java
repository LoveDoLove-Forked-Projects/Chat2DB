package ai.chat2db.community.domain.core.cache;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DomainCacheLifecycle {

    @PreDestroy
    public void close() {
        try {
            CacheManage.close();
        } catch (Exception e) {
            log.error("CacheManage close error", e);
        }
    }
}
