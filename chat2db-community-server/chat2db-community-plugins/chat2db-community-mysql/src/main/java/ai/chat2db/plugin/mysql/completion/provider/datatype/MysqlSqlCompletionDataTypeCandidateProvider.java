package ai.chat2db.plugin.mysql.completion.provider.datatype;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.config.completion.MysqlSqlCompletionDataTypeRuleConfig;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionInsertTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionDataTypeCandidateProvider {

    private static final List<DataTypeSpec> DATA_TYPES = List.of(
            dataType("INT"),
            dataType("INTEGER"),
            dataType("BIGINT"),
            dataType("TINYINT"),
            dataType("SMALLINT"),
            dataType("MEDIUMINT"),
            dataType("VARCHAR", "VARCHAR(${1:255})"),
            dataType("CHAR", "CHAR(${1:1})"),
            dataType("TEXT"),
            dataType("LONGTEXT"),
            dataType("DECIMAL", "DECIMAL(${1:10}, ${2:2})"),
            dataType("NUMERIC", "NUMERIC(${1:10}, ${2:2})"),
            dataType("DOUBLE"),
            dataType("FLOAT"),
            dataType("DATE"),
            dataType("DATETIME"),
            dataType("TIMESTAMP"),
            dataType("TIME"),
            dataType("YEAR"),
            dataType("BOOL"),
            dataType("BOOLEAN"),
            dataType("JSON"),
            dataType("BLOB"),
            dataType("LONGBLOB"),
            dataType("ENUM", "ENUM(${1:'value'})"),
            dataType("SET", "SET(${1:'value'})"));

    private MysqlSqlCompletionDataTypeCandidateProvider() {
    }

    public static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context,
                                                               SqlCompletionCandidates c3Result) {
        String prefix = context == null ? "" : context.prefix();
        if (StringUtils.isBlank(prefix)) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        if (!hasDataTypeEvidence(context, c3Result)) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        List<SqlCompletionCandidate> candidates = DATA_TYPES.stream()
                .filter(spec -> matchesPrefix(spec.label(), prefix))
                .map(MysqlSqlCompletionDataTypeCandidateProvider::candidate)
                .toList();
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static boolean hasDataTypeEvidence(MysqlSqlCompletionCandidateContext context,
                                               SqlCompletionCandidates c3Result) {
        if (context == null || context.dummySql() == null) {
            return false;
        }
        if (!MysqlSqlCompletionDataTypeRuleConfig.hasDataTypeRuleAtToken(c3Result, c3Result.tokenIndex())) {
            return false;
        }
        return context.dummySql().insertedLength() > 0
                || c3Result.tokenIndex() == tokenIndexAtReplaceStart(context);
    }

    private static int tokenIndexAtReplaceStart(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return -1;
        }
        CommonTokenStream tokenStream = MysqlSqlCompletionTokenUtil.tokenStream(context.window().parseSql());
        return MysqlSqlCompletionTokenUtil.tokenIndexAtOrAfterOffset(tokenStream, context.cursorContext().replaceStart());
    }

    private static boolean matchesPrefix(String label, String prefix) {
        return StringUtils.startsWithIgnoreCase(label, prefix);
    }

    private static SqlCompletionCandidate candidate(DataTypeSpec spec) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.TYPE,
                spec.label());
        candidate.setInsertText(spec.insertText());
        candidate.setSortRank(600);
        if (StringUtils.contains(spec.insertText(), "${")) {
            candidate.setInsertType(SqlCompletionInsertTypeEnum.SNIPPET);
        }
        return candidate;
    }

    private static DataTypeSpec dataType(String label) {
        return dataType(label, label);
    }

    private static DataTypeSpec dataType(String label, String insertText) {
        return new DataTypeSpec(label, insertText);
    }

    private record DataTypeSpec(String label,
                                String insertText) {
    }
}
