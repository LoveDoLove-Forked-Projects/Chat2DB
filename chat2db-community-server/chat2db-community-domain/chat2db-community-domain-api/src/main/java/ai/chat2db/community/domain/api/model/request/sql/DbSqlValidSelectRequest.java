package ai.chat2db.community.domain.api.model.request.sql;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DbSqlValidSelectRequest {

    @NotBlank
    private String sql;

    private String dbType;
}
