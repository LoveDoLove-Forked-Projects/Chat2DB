package ai.chat2db.community.domain.api.model.datasource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceConnect {




    private Boolean success;




    private String message;




    private String description;




    private String errorDetail;
}
