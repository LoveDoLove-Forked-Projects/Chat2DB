package ai.chat2db.community.domain.api.model.db;

import lombok.Data;
import java.util.List;


@Data
public class SimpleTableColumnMapping {

    private SimpleTable simpleTable;
    private List<SimpleColumn> simpleColumns;
}
