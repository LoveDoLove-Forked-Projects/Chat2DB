package ai.chat2db.community.web.api.model.request.driver;

import java.util.List;

import lombok.Data;

@Data
public class JdbcDriverRequest {
    String jdbcDriverClass;
    String dbType;

    List<String> jdbcDriver;
}
