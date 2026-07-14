package ai.chat2db.community.domain.api.model.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlContextParser {

    private List<SqlStatement> sqlStatementList;
    private List<MarkMessage> markMessageList;

}
