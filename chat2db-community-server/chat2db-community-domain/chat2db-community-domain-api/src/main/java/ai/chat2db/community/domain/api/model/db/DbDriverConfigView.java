package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DbDriverConfigView {

    private DBConfig dbConfig;

    private List<DriverConfig> availableDrivers;
}
