package ai.chat2db.community.domain.api.model.sql;

import ai.chat2db.community.domain.api.model.db.SimpleDatabase;
import ai.chat2db.community.domain.api.model.db.SimpleFunction;
import ai.chat2db.community.domain.api.model.db.SimpleProcedure;
import ai.chat2db.community.domain.api.model.db.SimpleSchema;
import ai.chat2db.community.domain.api.model.db.SimpleTable;
import ai.chat2db.community.domain.api.model.db.SimpleView;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlKeyword {
    private List<SimpleDatabase> databases;
    private List<SimpleSchema> schemas;
    private List<SimpleTable> tables;
    private List<SimpleView> views;
    private List<SimpleFunction> functions;
    private List<SimpleProcedure> procedures;

}
