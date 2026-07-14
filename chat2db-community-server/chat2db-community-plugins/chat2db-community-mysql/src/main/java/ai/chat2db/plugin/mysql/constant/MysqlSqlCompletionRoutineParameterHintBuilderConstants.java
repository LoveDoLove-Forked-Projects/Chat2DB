package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionEditorHintTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionEditorHint;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;



public final class MysqlSqlCompletionRoutineParameterHintBuilderConstants {

    public static final int MAX_ROUTINE_PARAMETER_HINT_LOOKUPS = 20;

    private MysqlSqlCompletionRoutineParameterHintBuilderConstants() {
    }
}
