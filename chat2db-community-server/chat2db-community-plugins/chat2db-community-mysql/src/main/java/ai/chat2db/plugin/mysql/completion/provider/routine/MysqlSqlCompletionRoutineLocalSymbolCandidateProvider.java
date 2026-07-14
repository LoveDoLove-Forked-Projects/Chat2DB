package ai.chat2db.plugin.mysql.completion.provider.routine;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.completion.analysis.statement.ddl.trigger.MysqlCreateTriggerStatementPseudoRecordAnalyzer;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalVariable;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionLocalSymbolSourceTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionRoutineLocalSymbolCandidateProvider {

    private MysqlSqlCompletionRoutineLocalSymbolCandidateProvider() {
    }

    public static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        String parseSql = parseSql(context);
        List<Token> tokens = MysqlSqlCompletionTokenUtil.significantDefaultTokens(parseSql);
        int cursor = Math.max(0, Math.min(context.replaceStart(), parseSql.length()));
        Map<String, SqlCompletionCandidate> candidates = new LinkedHashMap<>();
        collectLocalVariables(context, candidates);
        collectTriggerPseudoRecords(context, tokens, cursor, candidates);
        return MysqlSqlCompletionCandidateBuildResult.success(new ArrayList<>(candidates.values()));
    }

    private static String parseSql(MysqlSqlCompletionCandidateContext context) {
        if (context.input() != null && StringUtils.isNotBlank(context.input().parseSql())) {
            return context.input().parseSql();
        }
        return context.window().parseSql();
    }

    private static void collectTriggerPseudoRecords(MysqlSqlCompletionCandidateContext context,
                                                    List<Token> tokens,
                                                    int cursor,
                                                    Map<String, SqlCompletionCandidate> candidates) {
        if (!MysqlCreateTriggerStatementPseudoRecordAnalyzer.insideTriggerRoutineBody(tokens, cursor)) {
            return;
        }
        int event = triggerEvent(tokens, cursor);
        if (event == MySqlLexer.INSERT || event == MySqlLexer.UPDATE) {
            addPseudoRecord("NEW", context.prefix(), candidates);
        }
        if (event == MySqlLexer.DELETE || event == MySqlLexer.UPDATE) {
            addPseudoRecord("OLD", context.prefix(), candidates);
        }
    }

    private static void collectLocalVariables(MysqlSqlCompletionCandidateContext context,
                                              Map<String, SqlCompletionCandidate> candidates) {
        if (context.localContext() == null) {
            return;
        }
        for (SqlCompletionLocalVariable variable : context.localContext().variables()) {
            addVariable(context, variable, candidates);
        }
    }

    private static void addVariable(MysqlSqlCompletionCandidateContext context,
                                    SqlCompletionLocalVariable variable,
                                    Map<String, SqlCompletionCandidate> candidates) {
        if (variable == null || StringUtils.isBlank(variable.name())) {
            return;
        }
        if (!matchesVariablePrefix(context, variable)) {
            return;
        }
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.VARIABLE,
                variable.name());
        SqlCompletionLocalSymbolSourceTypeEnum sourceType = sourceType(variable);
        if (sourceType == SqlCompletionLocalSymbolSourceTypeEnum.USER_VARIABLE
                && userVariableAtAlreadyTyped(context)) {
            candidate.setInsertText(variable.name().substring(1));
        }
        candidate.setObjectName(variable.name());
        candidate.setDataType(variable.dataType());
        candidate.setDetail(variable.dataType());
        candidate.setSortRank(110);
        candidates.putIfAbsent(variable.name().toLowerCase(), candidate);
    }

    private static boolean matchesVariablePrefix(MysqlSqlCompletionCandidateContext context,
                                                 SqlCompletionLocalVariable variable) {
        String name = variable.name();
        String normalizedPrefix = StringUtils.defaultString(context.prefix());
        if (sourceType(variable) == SqlCompletionLocalSymbolSourceTypeEnum.USER_VARIABLE) {
            return matchesUserVariablePrefix(context, name, normalizedPrefix);
        }
        return StringUtils.startsWithIgnoreCase(name, normalizedPrefix);
    }

    private static boolean matchesUserVariablePrefix(MysqlSqlCompletionCandidateContext context,
                                                     String name,
                                                     String normalizedPrefix) {
        if (!userVariableNamePrefixStarted(context, normalizedPrefix)) {
            return false;
        }
        if (StringUtils.startsWithIgnoreCase(name, normalizedPrefix)) {
            return true;
        }
        return name.startsWith("@")
                && !normalizedPrefix.startsWith("@")
                && StringUtils.startsWithIgnoreCase(name.substring(1), normalizedPrefix);
    }

    private static boolean userVariableNamePrefixStarted(MysqlSqlCompletionCandidateContext context,
                                                         String normalizedPrefix) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return false;
        }
        if (StringUtils.startsWith(normalizedPrefix, "@")) {
            return StringUtils.length(normalizedPrefix) > 1;
        }
        if (userVariableAtAlreadyTyped(context)) {
            return true;
        }
        List<Token> tokens = MysqlSqlCompletionTokenUtil.tokens(context.window().parseSql());
        Token token = MysqlSqlCompletionTokenUtil.tokenCoveringRange(tokens, context.cursorContext().replaceStart(),
                Math.max(context.cursorContext().replaceStart(), context.window().cursor()));
        return token != null && token.getType() == MySqlLexer.LOCAL_ID;
    }

    private static boolean userVariableAtAlreadyTyped(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return false;
        }
        String sourceSql = StringUtils.defaultString(context.window().sourceSql());
        int replaceStart = Math.max(0, Math.min(windowReplaceStart(context), sourceSql.length()));
        return replaceStart > 0 && sourceSql.charAt(replaceStart - 1) == '@';
    }

    private static SqlCompletionLocalSymbolSourceTypeEnum sourceType(SqlCompletionLocalVariable variable) {
        return variable == null ? SqlCompletionLocalSymbolSourceTypeEnum.USER_VARIABLE
                : SqlCompletionLocalSymbolSourceTypeEnum.from(variable.sourceType(),
                SqlCompletionLocalSymbolSourceTypeEnum.USER_VARIABLE);
    }

    private static int windowReplaceStart(MysqlSqlCompletionCandidateContext context) {
        int replaceStart = context.cursorContext().replaceStart();
        int sourceStartOffset = context.window().sourceStartOffset();
        if (sourceStartOffset > 0 && replaceStart >= sourceStartOffset) {
            return replaceStart - sourceStartOffset;
        }
        return replaceStart;
    }

    private static int triggerEvent(List<Token> tokens, int cursor) {
        int triggerIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBefore(tokens, cursor, MySqlLexer.TRIGGER);
        if (triggerIndex < 0) {
            return -1;
        }
        int endIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, triggerIndex + 1, cursor,
                MySqlLexer.ON);
        if (endIndex < 0) {
            endIndex = tokens.size();
        }
        for (int index = triggerIndex + 1; index < Math.min(endIndex, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (!MysqlSqlCompletionTokenUtil.isDefaultToken(token)) {
                continue;
            }
            if (token.getType() == MySqlLexer.INSERT
                    || token.getType() == MySqlLexer.UPDATE
                    || token.getType() == MySqlLexer.DELETE) {
                return token.getType();
            }
        }
        return -1;
    }

    private static void addPseudoRecord(String name,
                                        String prefix,
                                        Map<String, SqlCompletionCandidate> candidates) {
        if (!StringUtils.startsWithIgnoreCase(name, StringUtils.defaultString(prefix))) {
            return;
        }
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD, name);
        candidate.setObjectName(name);
        candidate.setSortRank(80);
        candidates.putIfAbsent(name.toLowerCase(), candidate);
    }

}
