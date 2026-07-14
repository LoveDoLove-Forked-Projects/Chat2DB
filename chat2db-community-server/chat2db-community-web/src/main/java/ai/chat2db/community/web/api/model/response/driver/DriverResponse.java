package ai.chat2db.community.web.api.model.response.driver;

import ai.chat2db.community.domain.api.config.DriverConfig;
import lombok.Data;

import java.util.List;

@Data
public class DriverResponse {
    private String dbType;


    private String name;


    private DriverConfig defaultDriverConfig;


    private List<DriverConfig> driverConfigList;

}
