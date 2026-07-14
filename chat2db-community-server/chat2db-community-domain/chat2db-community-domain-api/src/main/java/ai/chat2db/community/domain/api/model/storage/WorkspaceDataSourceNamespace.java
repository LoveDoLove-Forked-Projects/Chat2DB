package ai.chat2db.community.domain.api.model.storage;

import lombok.Data;

import java.util.List;

@Data
public class WorkspaceDataSourceNamespace {

    private List<WorkspaceNamespace> namespaces;

    private List<WorkspaceDataSource> dataSources;
}
