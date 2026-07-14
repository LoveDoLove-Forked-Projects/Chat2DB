package ai.chat2db.community.domain.api.model.view;

import ai.chat2db.community.domain.api.model.form.FormConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyViewConfiguration {


    private List<FormConfig> configurations;
    private String sql;
    private String previewSql;


}
