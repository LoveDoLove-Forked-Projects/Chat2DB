
package ai.chat2db.community.domain.api.model.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Schema {




    @JsonAlias({"TABLE_CATALOG","table_catalog"})
    private String databaseName;



    @JsonAlias({"TABLE_SCHEM","table_schem"})
    private String name;


    private String comment;


    private String owner;

    private boolean system;
}
