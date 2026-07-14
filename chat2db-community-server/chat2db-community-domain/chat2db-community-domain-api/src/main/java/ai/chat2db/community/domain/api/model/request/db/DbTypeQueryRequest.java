package ai.chat2db.community.domain.api.model.request.db;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DbTypeQueryRequest {


    @NotNull
    private Long dataSourceId;

}
