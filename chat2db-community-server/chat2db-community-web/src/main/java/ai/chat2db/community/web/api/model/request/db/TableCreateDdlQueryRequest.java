package ai.chat2db.community.web.api.model.request.db;

import jakarta.validation.constraints.NotNull;

import lombok.Data;


@Data
public class TableCreateDdlQueryRequest {


    @NotNull
    private String dbType;


}
