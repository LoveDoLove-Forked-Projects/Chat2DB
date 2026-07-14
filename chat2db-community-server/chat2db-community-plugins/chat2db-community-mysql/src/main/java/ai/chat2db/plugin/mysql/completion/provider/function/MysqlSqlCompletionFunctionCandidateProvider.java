package ai.chat2db.plugin.mysql.completion.provider.function;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.completion.catalog.MysqlSqlCompletionFunctionCatalog;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionInsertTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionFunctionSpec;
import ai.chat2db.plugin.mysql.completion.provider.callable.MysqlSqlCompletionCallableCandidateSupport;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionFunctionCandidateProvider {

    private MysqlSqlCompletionFunctionCandidateProvider() {
    }

    static SqlCompletionCandidate candidate(SqlCompletionFunctionSpec spec) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.FUNCTION,
                spec.name());
        candidate.setInsertType(SqlCompletionInsertTypeEnum.SNIPPET);
        candidate.setInsertText(MysqlSqlCompletionCallableCandidateSupport.callableSnippetForParameterNames(
                spec.name(), parameterPlaceholders(spec.parameters())));
        candidate.setDetail("(" + spec.parameters() + ")");
        candidate.setDescription(spec.returnType());
        candidate.setDataType(spec.returnType());
        candidate.setObjectName(spec.name());
        candidate.setSortRank(700);
        return candidate;
    }

    public static List<SqlCompletionCandidate> build(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.cursorContext() == null) {
            return List.of();
        }
        return MysqlSqlCompletionFunctionCatalog.functions(context.prefix()).stream()
                .map(MysqlSqlCompletionFunctionCandidateProvider::candidate)
                .toList();
    }

    public static SqlCompletionCandidate fromToken(Integer tokenType,
                                                   List<Integer> ruleList,
                                                   SqlCompletionCandidates c3Result) {
        SqlCompletionFunctionSpec spec = MysqlSqlCompletionFunctionCatalog.function(tokenType, ruleList, c3Result);
        if (spec == null) {
            return null;
        }
        SqlCompletionCandidate candidate = candidate(spec);
        candidate.setInsertText(MysqlSqlCompletionCallableCandidateSupport.callableSnippet(spec.name()));
        return candidate;
    }

    private static List<String> parameterPlaceholders(String parameters) {
        if (StringUtils.isBlank(parameters)) {
            return List.of();
        }
        List<String> placeholders = new ArrayList<>();
        for (String segment : topLevelSegments(parameters)) {
            String placeholder = parameterPlaceholder(segment);
            if (StringUtils.isNotBlank(placeholder)) {
                placeholders.add(placeholder);
            }
        }
        return placeholders;
    }

    private static List<String> topLevelSegments(String value) {
        List<String> segments = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int bracketDepth = 0;
        int braceDepth = 0;
        for (int index = 0; index < value.length(); index++) {
            char ch = value.charAt(index);
            if (ch == '[') {
                bracketDepth++;
            } else if (ch == ']') {
                bracketDepth = Math.max(0, bracketDepth - 1);
            } else if (ch == '{') {
                braceDepth++;
            } else if (ch == '}') {
                braceDepth = Math.max(0, braceDepth - 1);
            }
            if (ch == ',' && bracketDepth == 0 && braceDepth == 0) {
                segments.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        segments.add(current.toString());
        return segments;
    }

    private static String parameterPlaceholder(String segment) {
        String normalized = StringUtils.defaultString(segment)
                .replace("[", "")
                .replace("]", "")
                .replace("{", "")
                .replace("}", "")
                .replace("*", "")
                .trim();
        if (StringUtils.isBlank(normalized)) {
            return "";
        }
        int colonIndex = normalized.indexOf(':');
        if (colonIndex > 0) {
            return normalized.substring(0, colonIndex).trim();
        }
        int spaceIndex = normalized.indexOf(' ');
        return spaceIndex > 0 ? normalized.substring(0, spaceIndex).trim() : normalized;
    }
}
