package ai.chat2db.community.domain.api.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectOption {

    private String label;
    private Integer value;
}
