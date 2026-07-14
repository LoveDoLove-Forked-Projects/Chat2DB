package ai.chat2db.community.domain.api.model.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineOperation {

    private String databaseName;

    private String schemaName;

    private String routineType;

    private String routineName;




    private String ddl;
}
