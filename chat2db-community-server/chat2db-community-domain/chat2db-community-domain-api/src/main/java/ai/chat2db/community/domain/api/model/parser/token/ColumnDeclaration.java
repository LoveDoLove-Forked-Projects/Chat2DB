package ai.chat2db.community.domain.api.model.parser.token;


import lombok.Data;

import java.util.List;

@Data
public class ColumnDeclaration {

    private Column column;
    private List<Constraint> constraints;


}
