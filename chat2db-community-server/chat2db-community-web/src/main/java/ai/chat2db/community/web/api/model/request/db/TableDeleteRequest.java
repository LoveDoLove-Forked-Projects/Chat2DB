package ai.chat2db.community.web.api.model.request.db;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;

import lombok.Data;


@Data
public class TableDeleteRequest extends DataSourceBaseRequest {


    @NotNull
    private String tableName;
}
