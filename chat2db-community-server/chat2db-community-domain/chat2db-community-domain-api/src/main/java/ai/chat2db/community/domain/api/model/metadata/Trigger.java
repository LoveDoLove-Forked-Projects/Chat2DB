
package ai.chat2db.community.domain.api.model.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Trigger {

    private String databaseName;

    private String schemaName;

    private String triggerName;

    private String eventManipulation;

    private String triggerBody;

}
