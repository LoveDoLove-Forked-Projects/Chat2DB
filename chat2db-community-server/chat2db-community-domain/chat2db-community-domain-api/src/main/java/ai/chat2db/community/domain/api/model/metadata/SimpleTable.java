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
public class SimpleTable {



    @JsonAlias({"TABLE_NAME"})
    private String name;




    @JsonAlias({"REMARKS"})
    private String comment;


    private String tableType;
}
