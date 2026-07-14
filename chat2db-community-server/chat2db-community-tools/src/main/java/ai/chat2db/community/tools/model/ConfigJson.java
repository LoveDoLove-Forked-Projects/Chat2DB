package ai.chat2db.community.tools.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigJson {


    private String latestStartupSuccessVersion;


    private String jwtSecretKey;


    private String systemUuid;

    private String networkStatus;
}
