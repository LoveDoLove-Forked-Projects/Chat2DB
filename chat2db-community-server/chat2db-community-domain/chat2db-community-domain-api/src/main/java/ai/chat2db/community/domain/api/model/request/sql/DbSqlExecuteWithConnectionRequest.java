package ai.chat2db.community.domain.api.model.request.sql;

import java.sql.Connection;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbSqlExecuteWithConnectionRequest {

    @NotBlank
    private String sql;

    @NotNull
    private Connection connection;

    private boolean offset;

    private int pageNo;

    private int pageSize;
}
