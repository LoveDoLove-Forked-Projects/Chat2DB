package ai.chat2db.community.domain.api.model.datasource;

import ai.chat2db.community.domain.api.model.workspace.Namespace;
import java.util.List;
import lombok.Data;

@Data
public class DataSourceNamespace {


    private List<Namespace> namespaces;


    private List<DataSource> dataSources;
}
