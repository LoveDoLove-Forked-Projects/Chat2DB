package ai.chat2db.plugin.mysql.completion.provider.metadata;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionDatabaseQualifierCandidateDecorator {

    private MysqlSqlCompletionDatabaseQualifierCandidateDecorator() {
    }

    public static List<SqlCompletionCandidate> decorate(List<SqlCompletionCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream()
                .map(MysqlSqlCompletionDatabaseQualifierCandidateDecorator::decorate)
                .toList();
    }

    public static SqlCompletionCandidate decorate(SqlCompletionCandidate candidate) {
        if (candidate == null || candidate.getType() != SqlCompletionCandidateTypeEnum.DATABASE) {
            return candidate;
        }
        String name = candidateName(candidate);
        if (StringUtils.isBlank(name)) {
            return candidate;
        }
        SqlCompletionCandidate decorated = copy(candidate);
        decorated.setInsertText(name + ".");
        return decorated;
    }

    private static SqlCompletionCandidate copy(SqlCompletionCandidate candidate) {
        SqlCompletionCandidate copied = new SqlCompletionCandidate();
        copied.setId(candidate.getId());
        copied.setLabel(candidate.getLabel());
        copied.setType(candidate.getType());
        copied.setInsertText(candidate.getInsertText());
        copied.setInsertType(candidate.getInsertType());
        copied.setReplaceStart(candidate.getReplaceStart());
        copied.setReplaceEnd(candidate.getReplaceEnd());
        copied.setDetail(candidate.getDetail());
        copied.setDescription(candidate.getDescription());
        copied.setDataType(candidate.getDataType());
        copied.setObjectType(candidate.getObjectType());
        copied.setComment(candidate.getComment());
        copied.setDatasourceName(candidate.getDatasourceName());
        copied.setDatabaseName(candidate.getDatabaseName());
        copied.setSchemaName(candidate.getSchemaName());
        copied.setTableName(candidate.getTableName());
        copied.setTableAlias(candidate.getTableAlias());
        copied.setColumnName(candidate.getColumnName());
        copied.setObjectName(candidate.getObjectName());
        copied.setParameterMode(candidate.getParameterMode());
        copied.setSortRank(candidate.getSortRank());
        copied.setSortText(candidate.getSortText());
        copied.setSnippetSlots(candidate.getSnippetSlots());
        return copied;
    }

    private static String candidateName(SqlCompletionCandidate candidate) {
        return StringUtils.defaultIfBlank(candidate.getDatabaseName(),
                StringUtils.defaultIfBlank(candidate.getSchemaName(),
                        StringUtils.defaultIfBlank(candidate.getObjectName(),
                                StringUtils.defaultIfBlank(candidate.getTableName(), candidate.getLabel()))));
    }
}
