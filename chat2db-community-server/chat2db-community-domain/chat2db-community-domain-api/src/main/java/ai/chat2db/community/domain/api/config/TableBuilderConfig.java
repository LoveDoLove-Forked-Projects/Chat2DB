package ai.chat2db.community.domain.api.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableBuilderConfig {




    private Boolean needFullTableName;

    public static TableBuilderConfig defaultConfig() {
        return TableBuilderConfig.builder()
                .needFullTableName(Boolean.FALSE)
                .build();
    }

}
