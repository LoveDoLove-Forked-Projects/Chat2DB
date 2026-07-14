package ai.chat2db.community.domain.api.model.request.datasource;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DbDataSourceCloseRequest {


    @NotNull
    private Long dataSourceId;

}
