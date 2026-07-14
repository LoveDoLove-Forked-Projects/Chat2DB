
package ai.chat2db.spi.model.datasource;
import java.sql.Driver;

import ai.chat2db.community.domain.api.config.DriverConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DriverEntry {

    private DriverConfig driverConfig;

    private Driver driver;

}
