package ai.chat2db.community.domain.api.model.workspace;

import ai.chat2db.community.domain.api.model.datasource.DataSource;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Namespace {


    private Long id;


    private String name;


    private String description;


    private List<DataSource> dataSources;


    private List<Long> datasourceIds;


    private Long parentId;
}
