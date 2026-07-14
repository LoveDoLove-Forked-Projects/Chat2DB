package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionInsertTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;



public final class MysqlSqlCompletionCallableCandidateSupportConstants {

    public static final int MAX_ROUTINE_PARAMETER_LOOKUPS = 20;
    public static final int METADATA_FUNCTION_SORT_RANK = 650;
    public static final String ARGUMENT_SEPARATOR = ", ";
    public static final String OPEN_PAREN = "(";
    public static final String CLOSE_PAREN = ")";
    public static final String TYPE_SEPARATOR = ":";

    private MysqlSqlCompletionCallableCandidateSupportConstants() {
    }
}
