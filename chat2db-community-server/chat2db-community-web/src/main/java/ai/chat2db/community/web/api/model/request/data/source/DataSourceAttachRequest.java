package ai.chat2db.community.web.api.model.request.data.source;


import jakarta.validation.constraints.NotNull;

import lombok.Data;


@Data
public class DataSourceAttachRequest implements IDataSourceBaseRequestInfo{


    @NotNull
    private Long id;

    @Override
    public Long getDataSourceId() {
        return id;
    }

    @Override
    public String getDatabaseName() {
        return null;
    }
}
