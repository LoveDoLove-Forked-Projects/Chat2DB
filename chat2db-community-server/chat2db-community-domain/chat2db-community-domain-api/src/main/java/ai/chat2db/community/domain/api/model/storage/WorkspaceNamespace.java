package ai.chat2db.community.domain.api.model.storage;

import lombok.Data;

import java.util.List;

@Data
public class WorkspaceNamespace {

    private Long id;

    private String name;

    private Long order;

    private Boolean editable;

    private List<WorkspaceDataSource> dataSources;
}
