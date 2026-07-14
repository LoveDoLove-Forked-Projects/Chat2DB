package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionInsertColumnListContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;



public final class MysqlSqlCompletionInsertColumnListCandidateFilterConstants {

    public static final int ALL_REMAINING_COLUMNS_SORT_RANK = -100;

    private MysqlSqlCompletionInsertColumnListCandidateFilterConstants() {
    }
}
