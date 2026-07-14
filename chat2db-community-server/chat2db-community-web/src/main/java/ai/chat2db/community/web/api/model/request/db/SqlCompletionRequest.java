package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.domain.api.model.completion.SqlCompletionActiveSnippetSlot;
import ai.chat2db.community.web.api.model.request.data.source.ConsoleCloseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlCompletionRequest extends ConsoleCloseRequest {
    private String sql;
    private Integer cursor;
    private String beforeSql;
    private String afterSql;
    private int currentRowNum;
    private int currentColNum;
    private boolean needFullName;
    private String keywordCase;
    private SqlCompletionActiveSnippetSlot activeSnippetSlot;
}
