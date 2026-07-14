
package ai.chat2db.community.domain.api.model.request.db;

import java.sql.Connection;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DbSchemaQueryRequest {

    @NotNull
    private Long dataSourceId;

    private String dataBaseName;


    private boolean refresh;


    private Connection connection;
}
