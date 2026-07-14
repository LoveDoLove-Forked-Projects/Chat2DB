package ai.chat2db.community.domain.api.model.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Charset {

    private String charsetName;

    private String defaultCollationName;
}
