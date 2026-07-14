
package ai.chat2db.community.domain.api.config;
import java.util.List;

import ai.chat2db.community.domain.api.model.datasource.KeyValue;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


@Data
public class DriverConfig {




    private String url;



    private String jdbcDriver;




    private String jdbcDriverClass;




    private List<String> downloadJdbcDriverUrls;




    private String dbType;




    private boolean custom;




    private List<KeyValue> extendInfo;


    private boolean defaultDriver;

    public boolean notEmpty() {
       return StringUtils.isNotBlank(getJdbcDriver()) && StringUtils.isNotBlank(
            getJdbcDriverClass());
    }
}
