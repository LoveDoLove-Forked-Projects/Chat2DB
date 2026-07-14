package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.domain.api.model.db.SimpleIdentifier;
import ai.chat2db.community.domain.api.model.sql.SqlStatement;
import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlHoverRequest extends DataSourceBaseRequest {


    private SqlStatement currentStatement;


    private SimpleIdentifier hoverIdentifier;
}
