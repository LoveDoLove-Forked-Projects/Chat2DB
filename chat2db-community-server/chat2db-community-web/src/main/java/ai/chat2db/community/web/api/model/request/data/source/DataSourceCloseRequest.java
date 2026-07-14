package ai.chat2db.community.web.api.model.request.data.source;


import jakarta.validation.constraints.NotNull;

import lombok.Data;


@Data
public class DataSourceCloseRequest {


    @NotNull
    private Long id;

}
