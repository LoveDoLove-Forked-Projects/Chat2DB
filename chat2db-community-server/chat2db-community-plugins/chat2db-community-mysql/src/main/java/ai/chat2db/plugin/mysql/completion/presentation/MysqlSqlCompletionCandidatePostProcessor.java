package ai.chat2db.plugin.mysql.completion.presentation;

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


import static ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionCandidatePostProcessorConstants.*;
public final class MysqlSqlCompletionCandidatePostProcessor {

    private static final Map<SqlCompletionCandidateTypeEnum, Integer> TYPE_RANKS = typeRanks();

    private MysqlSqlCompletionCandidatePostProcessor() {
    }

    public static List<SqlCompletionCandidate> process(List<SqlCompletionCandidate> candidates,
                                                       SqlCompletionKeywordCaseEnum keywordCase) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        Map<String, SqlCompletionCandidate> unique = new LinkedHashMap<>();
        for (SqlCompletionCandidate candidate : candidates) {
            if (candidate == null || StringUtils.isBlank(candidate.getLabel())) {
                continue;
            }
            if (StringUtils.isBlank(candidate.getInsertText())) {
                candidate.setInsertText(candidate.getLabel());
            }
            unique.merge(identity(candidate), candidate, MysqlSqlCompletionCandidatePostProcessor::preferred);
        }
        List<SqlCompletionCandidate> processed = unique.values().stream()
                .sorted(Comparator
                        .comparingInt(MysqlSqlCompletionCandidatePostProcessor::typeRank)
                        .thenComparingInt(candidate -> Objects.requireNonNullElse(candidate.getSortRank(), Integer.MAX_VALUE))
                        .thenComparing(SqlCompletionCandidate::getLabel, String.CASE_INSENSITIVE_ORDER))
                .toList();
        for (int i = 0; i < processed.size(); i++) {
            processed.get(i).setSortText("mysql_" + StringUtils.leftPad(String.valueOf(i), 5, '0'));
        }
        applyKeywordCase(processed, keywordCase);
        return processed;
    }

    private static void applyKeywordCase(List<SqlCompletionCandidate> candidates,
                                         SqlCompletionKeywordCaseEnum keywordCase) {
        SqlCompletionKeywordCaseEnum resolvedKeywordCase = keywordCase == null ? SqlCompletionKeywordCaseEnum.LOWER
                : keywordCase;
        for (SqlCompletionCandidate candidate : candidates) {
            if (candidate == null || !keywordCaseApplies(candidate)) {
                continue;
            }
            candidate.setLabel(applyCase(candidate.getLabel(), resolvedKeywordCase));
            candidate.setInsertText(applyCase(candidate.getInsertText(), resolvedKeywordCase));
        }
    }

    private static boolean keywordCaseApplies(SqlCompletionCandidate candidate) {
        return candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                || candidate.getType() == SqlCompletionCandidateTypeEnum.SNIPPET;
    }

    private static String applyCase(String value, SqlCompletionKeywordCaseEnum keywordCase) {
        if (value == null) {
            return null;
        }
        if (keywordCase == SqlCompletionKeywordCaseEnum.UPPER) {
            return value.toUpperCase(Locale.ROOT);
        }
        return value.toLowerCase(Locale.ROOT);
    }

    private static String identity(SqlCompletionCandidate candidate) {
        if (candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION) {
            return functionIdentity(candidate);
        }
        return String.join("|",
                Objects.toString(candidate.getType(), SqlCompletionCandidateTypeEnum.OTHER.name()),
                normalize(candidate.getDatabaseName()),
                normalize(candidate.getSchemaName()),
                normalize(candidate.getTableName()),
                normalize(candidate.getTableAlias()),
                normalize(candidate.getColumnName()),
                normalize(candidate.getInsertText()));
    }

    private static String functionIdentity(SqlCompletionCandidate candidate) {
        return String.join("|",
                Objects.toString(candidate.getType(), SqlCompletionCandidateTypeEnum.OTHER.name()),
                normalize(candidate.getDatabaseName()),
                normalize(candidate.getSchemaName()),
                normalize(StringUtils.defaultIfBlank(candidate.getObjectName(), candidate.getLabel())));
    }

    private static SqlCompletionCandidate preferred(SqlCompletionCandidate existing, SqlCompletionCandidate candidate) {
        return candidateRank(candidate) < candidateRank(existing) ? candidate : existing;
    }

    private static int candidateRank(SqlCompletionCandidate candidate) {
        return Objects.requireNonNullElse(candidate.getSortRank(), Integer.MAX_VALUE);
    }

    private static int typeRank(SqlCompletionCandidate candidate) {
        return TYPE_RANKS.getOrDefault(candidate.getType(), DEFAULT_TYPE_RANK);
    }

    private static Map<SqlCompletionCandidateTypeEnum, Integer> typeRanks() {
        Map<SqlCompletionCandidateTypeEnum, Integer> ranks = new EnumMap<>(SqlCompletionCandidateTypeEnum.class);
        ranks.put(SqlCompletionCandidateTypeEnum.VARIABLE, 8);
        ranks.put(SqlCompletionCandidateTypeEnum.ALIAS, 9);
        ranks.put(SqlCompletionCandidateTypeEnum.COLUMN, 10);
        ranks.put(SqlCompletionCandidateTypeEnum.ALL_COLUMN, 10);
        ranks.put(SqlCompletionCandidateTypeEnum.TABLE, 20);
        ranks.put(SqlCompletionCandidateTypeEnum.TEMP_TABLE, 20);
        ranks.put(SqlCompletionCandidateTypeEnum.VIEW, 21);
        ranks.put(SqlCompletionCandidateTypeEnum.MATERIALIZED_VIEW, 21);
        ranks.put(SqlCompletionCandidateTypeEnum.TABLE_VIEW, 21);
        ranks.put(SqlCompletionCandidateTypeEnum.FUNCTION, 25);
        ranks.put(SqlCompletionCandidateTypeEnum.PROCEDURE, 26);
        ranks.put(SqlCompletionCandidateTypeEnum.ROUTINE, 26);
        ranks.put(SqlCompletionCandidateTypeEnum.TYPE, 27);
        ranks.put(SqlCompletionCandidateTypeEnum.SCHEMA, 30);
        ranks.put(SqlCompletionCandidateTypeEnum.CATALOG, 30);
        ranks.put(SqlCompletionCandidateTypeEnum.DATABASE, 30);
        ranks.put(SqlCompletionCandidateTypeEnum.INDEX, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.EVENT, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.PARAMETER, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.USER, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.ROLE, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.TABLESPACE, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.TRIGGER, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.SEQUENCE, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.PACKAGE, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.CONSTRAINT, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.SYNONYM, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.DBLINK, 40);
        ranks.put(SqlCompletionCandidateTypeEnum.KEYWORD, 50);
        ranks.put(SqlCompletionCandidateTypeEnum.SNIPPET, 60);
        return Map.copyOf(ranks);
    }

    private static String normalize(String value) {
        return StringUtils.defaultString(value).toLowerCase(Locale.ROOT);
    }
}
