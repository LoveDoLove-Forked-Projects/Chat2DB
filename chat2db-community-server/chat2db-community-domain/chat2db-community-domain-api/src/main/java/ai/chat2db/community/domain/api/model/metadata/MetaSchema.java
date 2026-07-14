package ai.chat2db.community.domain.api.model.metadata;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetaSchema {



    private List<Database> databases;




    private List<Schema> schemas;
}
