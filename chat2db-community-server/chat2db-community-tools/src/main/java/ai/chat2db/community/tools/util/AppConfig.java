package ai.chat2db.community.tools.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppConfig {

    private String version;

    private String downloadUrl;

    private String networkStatus;

}
