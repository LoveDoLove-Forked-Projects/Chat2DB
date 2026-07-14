package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionKeywordCaseEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;



public final class MysqlSqlCompletionCandidatePostProcessorConstants {

    public static final int DEFAULT_TYPE_RANK = 100;

    private MysqlSqlCompletionCandidatePostProcessorConstants() {
    }
}
