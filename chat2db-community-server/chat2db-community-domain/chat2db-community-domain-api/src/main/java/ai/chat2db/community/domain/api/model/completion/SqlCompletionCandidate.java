package ai.chat2db.community.domain.api.model.completion;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionInsertTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionParameterModeTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionSnippetSlotTypeEnum;
import java.util.List;
import lombok.Data;


@Data
public class SqlCompletionCandidate {

    private String id;
    private String label;
    private SqlCompletionCandidateTypeEnum type = SqlCompletionCandidateTypeEnum.OTHER;
    private String insertText;
    private SqlCompletionInsertTypeEnum insertType = SqlCompletionInsertTypeEnum.PLAIN_TEXT;
    private Integer replaceStart;
    private Integer replaceEnd;
    private String detail;
    private String description;
    private String dataType;
    private String objectType;
    private String comment;
    private String datasourceName;
    private String databaseName;
    private String schemaName;
    private String tableName;
    private String tableAlias;
    private String columnName;
    private String objectName;
    private SqlCompletionParameterModeTypeEnum parameterMode;
    private Integer sortRank;
    private String sortText;
    private List<SqlCompletionSnippetSlotTypeEnum> snippetSlots;

    public static SqlCompletionCandidate of(SqlCompletionCandidateTypeEnum type, String label) {
        SqlCompletionCandidate candidate = new SqlCompletionCandidate();
        candidate.setType(type == null ? SqlCompletionCandidateTypeEnum.OTHER : type);
        candidate.setLabel(label);
        candidate.setInsertText(label);
        return candidate;
    }
}
