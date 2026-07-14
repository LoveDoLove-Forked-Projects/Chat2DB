package ai.chat2db.community.web.api.converter.driver;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.db.DbDriverConfigView;
import ai.chat2db.community.web.api.model.request.driver.JdbcDriverRequest;
import ai.chat2db.community.web.api.model.response.driver.DriverResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class JdbcDriverConverter {

    public abstract DriverResponse dbConfig2response(DBConfig dbConfig);

    public DriverResponse driverConfigView2response(DbDriverConfigView view) {
        DriverResponse response = dbConfig2response(view.getDbConfig());
        response.setDriverConfigList(view.getAvailableDrivers());
        return response;
    }

    public DriverConfig saveRequest2driverConfig(JdbcDriverRequest request) {
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setDbType(request.getDbType());
        driverConfig.setJdbcDriverClass(request.getJdbcDriverClass());
        driverConfig.setCustom(true);
        return driverConfig;
    }
}
