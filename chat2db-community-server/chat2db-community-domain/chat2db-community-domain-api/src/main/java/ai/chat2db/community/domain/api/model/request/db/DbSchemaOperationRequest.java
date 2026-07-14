
package ai.chat2db.community.domain.api.model.request.db;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DbSchemaOperationRequest {
    @NotBlank
    String databaseName;
    @NotBlank
    String schemaName;
    String newSchemaName;
}
