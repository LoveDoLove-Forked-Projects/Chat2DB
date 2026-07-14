package ai.chat2db.community.domain.api.model.request.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableSelector {


    private Boolean columnList;


    private Boolean indexList;

}
