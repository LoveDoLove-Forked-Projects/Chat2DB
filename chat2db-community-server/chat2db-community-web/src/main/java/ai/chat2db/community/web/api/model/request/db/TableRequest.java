package ai.chat2db.community.web.api.model.request.db;

import java.util.List;

import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableRequest {


    private String name;


    private String comment;


    private List<TableColumn> columnList;


    private List<TableIndex> indexList;


    private String schemaName;


    private String databaseName;


    private String engine;


    private String charset;


    private String collate;

    private Long incrementValue;

    private String partition;

    private boolean pinned;
}
