package ai.chat2db.community.tools.config;

import ai.chat2db.community.tools.enums.ModeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "chat2db")
@Data
public class Chat2dbProperties {


    private String version;

    private ModeEnum mode;

    private String appHost;

    private String cnAppHost;
}
