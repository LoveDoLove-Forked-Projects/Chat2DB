package ai.chat2db.community.web.api.model.response.data.source;

import lombok.Data;

import java.util.List;

@Data
public class DataSourceNamespaceResponse {


    private List<NamespaceResponse> namespaces;


    private List<DataSourceResponse> dataSources;
}
