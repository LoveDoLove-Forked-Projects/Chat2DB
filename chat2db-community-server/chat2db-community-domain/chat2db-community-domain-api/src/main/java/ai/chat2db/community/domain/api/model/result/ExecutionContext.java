package ai.chat2db.community.domain.api.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionContext {

    private String databaseName;

    private String schemaName;
}
