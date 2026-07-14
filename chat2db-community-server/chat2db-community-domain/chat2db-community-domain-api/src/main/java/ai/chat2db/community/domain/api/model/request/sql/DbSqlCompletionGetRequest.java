package ai.chat2db.community.domain.api.model.request.sql;

import ai.chat2db.community.domain.api.model.completion.SqlCompletionActiveSnippetSlot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbSqlCompletionGetRequest {

    private Long consoleId;
    @NotNull
    private Long dataSourceId;
    private String databaseName;
    private String schemaName;
    @NotBlank
    private String sql;
    @NotNull
    @Min(0)
    private Integer cursor;
    @Min(0)
    private Integer minPrefixLength;
    private Boolean needFullName;
    private String keywordCase;
    @Valid
    private SqlCompletionActiveSnippetSlot activeSnippetSlot;
}
