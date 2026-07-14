package ai.chat2db.community.domain.api.model.metadata;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Database {



    @JsonAlias({"TABLE_CAT"})
    private String name;




    private List<Schema> schemas;


    private String comment;

    private String charset;

    private String collation;

    private String owner;

    private boolean system;
}
