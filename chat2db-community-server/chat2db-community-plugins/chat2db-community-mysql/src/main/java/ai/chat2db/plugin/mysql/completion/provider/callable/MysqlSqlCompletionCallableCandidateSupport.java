package ai.chat2db.plugin.mysql.completion.provider.callable;

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


import static ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionCallableCandidateSupportConstants.*;
public final class MysqlSqlCompletionCallableCandidateSupport {







    private MysqlSqlCompletionCallableCandidateSupport() {
    }

    public static List<SqlCompletionCandidate> enrich(MysqlSqlCompletionCandidateContext context,
                                                      SqlCompletionCandidateTypeEnum type,
                                                      List<SqlCompletionCandidate> candidates) {
        ArgumentListAfterReplacement argumentListAfterReplacement = argumentListAfterReplacement(context);
        boolean enrichRoutineParameters = shouldEnrichRoutineParameters(context, candidates);
        return candidates.stream()
                .map(candidate -> enrich(context, type, candidate, argumentListAfterReplacement,
                        enrichRoutineParameters))
                .toList();
    }

    public static boolean hasArgumentListAfterReplacement(MysqlSqlCompletionCandidateContext context) {
        return argumentListAfterReplacement(context).present();
    }

    public static String callableSnippet(String label) {
        return callableSnippet(label, List.of());
    }

    static String callableSnippet(String label, List<SqlCompletionCandidate> parameters) {
        return callableSnippetForParameterNames(label, parameters == null ? List.of() : parameters.stream()
                .map(parameter -> parameterName(parameter, null))
                .toList());
    }

    public static String callableSnippetForParameterNames(String label, List<String> parameterNames) {
        String safeLabel = StringUtils.defaultString(label);
        if (parameterNames == null || parameterNames.isEmpty()) {
            return safeLabel + "(${1:})$0";
        }
        StringBuilder builder = new StringBuilder(safeLabel).append(OPEN_PAREN);
        for (int index = 0; index < parameterNames.size(); index++) {
            if (index > 0) {
                builder.append(ARGUMENT_SEPARATOR);
            }
            builder.append("${")
                    .append(index + 1)
                    .append(TYPE_SEPARATOR)
                    .append(escapeSnippetPlaceholder(StringUtils.defaultIfBlank(parameterNames.get(index),
                            "arg" + (index + 1))))
                    .append("}");
        }
        return builder.append(CLOSE_PAREN).append("$0").toString();
    }

    private static SqlCompletionCandidate enrich(MysqlSqlCompletionCandidateContext context,
                                                 SqlCompletionCandidateTypeEnum type,
                                                 SqlCompletionCandidate candidate,
                                                 ArgumentListAfterReplacement argumentListAfterReplacement,
                                                 boolean enrichRoutineParameters) {
        if (candidate == null) {
            return null;
        }
        String label = StringUtils.defaultString(candidate.getLabel());
        String insertName = StringUtils.defaultIfBlank(candidate.getInsertText(), label);
        List<SqlCompletionCandidate> parameters = enrichRoutineParameters
                ? routineParameters(context, type, candidate)
                : List.of();
        if (argumentListAfterReplacement.hasNonEmptyArgumentList()) {
            candidate.setInsertText(bareCallableInsertText(insertName, label));
            candidate.setInsertType(SqlCompletionInsertTypeEnum.PLAIN_TEXT);
        } else {
            candidate.setInsertText(hasCallableTemplate(insertName) ? insertName : callableSnippet(insertName, parameters));
            candidate.setInsertType(SqlCompletionInsertTypeEnum.SNIPPET);
            if (argumentListAfterReplacement.empty()) {
                candidate.setReplaceStart(context.replaceStart());
                candidate.setReplaceEnd(context.window().sourceStartOffset() + argumentListAfterReplacement.closeOffset() + 1);
            }
        }
        if (StringUtils.isBlank(candidate.getObjectName())) {
            candidate.setObjectName(label);
        }
        if (type == SqlCompletionCandidateTypeEnum.FUNCTION) {
            candidate.setSortRank(METADATA_FUNCTION_SORT_RANK);
        }
        String parameterDetail = routineParameterDetail(parameters);
        if (StringUtils.isNotBlank(parameterDetail)) {
            candidate.setDetail(parameterDetail);
        } else if (StringUtils.isBlank(candidate.getDetail())) {
            candidate.setDetail(OPEN_PAREN + CLOSE_PAREN);
        }
        return candidate;
    }

    private static ArgumentListAfterReplacement argumentListAfterReplacement(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return ArgumentListAfterReplacement.none();
        }
        String sql = context.window().sourceSql();
        int offset = Math.max(0, Math.min(context.cursorContext().replaceEnd(), sql.length()));
        while (offset < sql.length() && Character.isWhitespace(sql.charAt(offset))) {
            offset++;
        }
        if (offset >= sql.length() || sql.charAt(offset) != '(') {
            return ArgumentListAfterReplacement.none();
        }
        int closeOffset = matchingCloseParen(sql, offset);
        if (closeOffset < 0) {
            return ArgumentListAfterReplacement.nonEmpty(offset);
        }
        boolean empty = true;
        for (int index = offset + 1; index < closeOffset; index++) {
            if (!Character.isWhitespace(sql.charAt(index))) {
                empty = false;
                break;
            }
        }
        return empty
                ? ArgumentListAfterReplacement.empty(offset, closeOffset)
                : ArgumentListAfterReplacement.nonEmpty(offset);
    }

    private static int matchingCloseParen(String sql, int openOffset) {
        int depth = 0;
        for (int index = openOffset; index < sql.length(); index++) {
            char ch = sql.charAt(index);
            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
                if (depth == 0) {
                    return index;
                }
            } else if (ch == '\'' || ch == '"' || ch == '`') {
                index = quotedSegmentEnd(sql, index, ch);
            }
        }
        return -1;
    }

    private static int quotedSegmentEnd(String sql, int start, char quote) {
        for (int index = start + 1; index < sql.length(); index++) {
            char ch = sql.charAt(index);
            if (ch == '\\') {
                index++;
            } else if (ch == quote) {
                return index;
            }
        }
        return sql.length() - 1;
    }

    private static boolean shouldEnrichRoutineParameters(MysqlSqlCompletionCandidateContext context,
                                                         List<SqlCompletionCandidate> candidates) {
        if (context == null || context.metadataProvider() == null || candidates == null || candidates.isEmpty()) {
            return false;
        }
        return candidates.size() <= MAX_ROUTINE_PARAMETER_LOOKUPS;
    }

    private static List<SqlCompletionCandidate> routineParameters(MysqlSqlCompletionCandidateContext context,
                                                                  SqlCompletionCandidateTypeEnum type,
                                                                  SqlCompletionCandidate candidate) {
        if (context == null || context.metadataProvider() == null || candidate == null) {
            return List.of();
        }
        String routineName = StringUtils.defaultIfBlank(candidate.getObjectName(), candidate.getLabel());
        if (StringUtils.isBlank(routineName)) {
            return List.of();
        }
        SqlCompletionMetadataResponse result = context.metadataProvider().list(DbSqlCompletionMetadataRequest.of(
                SqlCompletionCandidateTypeEnum.PARAMETER,
                new SqlCompletionMetadataScope(candidate.getDatabaseName(), candidate.getSchemaName(), null,
                        routineName),
                "",
                type == SqlCompletionCandidateTypeEnum.PROCEDURE
                        ? SqlCompletionCandidateTypeEnum.PROCEDURE
                        : SqlCompletionCandidateTypeEnum.FUNCTION));
        if (result == null || !SqlCompletionStatusEnum.SUCCESS.name().equals(result.getStatus()) || result.getCandidates() == null) {
            return List.of();
        }
        return result.getCandidates().stream()
                .filter(Objects::nonNull)
                .filter(parameter -> StringUtils.isNotBlank(parameterName(parameter, null)))
                .sorted(Comparator.comparingInt(parameter -> parameter.getSortRank() == null
                        ? Integer.MAX_VALUE
                        : parameter.getSortRank()))
                .toList();
    }

    private static String routineParameterDetail(List<SqlCompletionCandidate> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        return parameters.stream()
                .map(parameter -> {
                    String detail = parameterName(parameter, null);
                    String type = parameterType(parameter);
                    if (StringUtils.isNotBlank(type)) {
                        detail += TYPE_SEPARATOR + type;
                    }
                    return detail;
                })
                .filter(StringUtils::isNotBlank)
                .collect(java.util.stream.Collectors.joining(ARGUMENT_SEPARATOR, OPEN_PAREN, CLOSE_PAREN));
    }

    private static String parameterName(SqlCompletionCandidate parameter, Integer fallbackIndex) {
        String name = StringUtils.defaultIfBlank(parameter.getColumnName(), parameter.getLabel());
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        return fallbackIndex == null ? "" : "arg" + (fallbackIndex + 1);
    }

    private static String parameterType(SqlCompletionCandidate parameter) {
        return StringUtils.defaultIfBlank(parameter.getDataType(), parameter.getDetail());
    }

    private static String escapeSnippetPlaceholder(String value) {
        return StringUtils.defaultString(value)
                .replace("\\", "\\\\")
                .replace("$", "\\$")
                .replace("}", "\\}");
    }

    private static boolean hasCallableTemplate(String insertText) {
        if (StringUtils.isBlank(insertText)) {
            return false;
        }
        String trimmedInsertText = insertText.trim();
        return insertText.contains("${")
                || insertText.contains("$0")
                || (trimmedInsertText.endsWith(")") && trimmedInsertText.indexOf('(') >= 0);
    }

    private static String bareCallableInsertText(String insertText, String label) {
        if (StringUtils.isBlank(insertText)) {
            return StringUtils.defaultString(label);
        }
        if (insertText.contains("${") || insertText.contains("$0")) {
            return StringUtils.defaultString(label);
        }
        String trimmedInsertText = insertText.trim();
        int openParenIndex = insertText.indexOf('(');
        if (trimmedInsertText.endsWith(")") && openParenIndex >= 0) {
            return insertText.substring(0, openParenIndex);
        }
        return insertText;
    }

    private record ArgumentListAfterReplacement(boolean present, boolean empty, int openOffset, int closeOffset) {

        static ArgumentListAfterReplacement none() {
            return new ArgumentListAfterReplacement(false, false, -1, -1);
        }

        static ArgumentListAfterReplacement empty(int openOffset, int closeOffset) {
            return new ArgumentListAfterReplacement(true, true, openOffset, closeOffset);
        }

        static ArgumentListAfterReplacement nonEmpty(int openOffset) {
            return new ArgumentListAfterReplacement(true, false, openOffset, -1);
        }

        boolean hasNonEmptyArgumentList() {
            return present && !empty;
        }
    }
}
