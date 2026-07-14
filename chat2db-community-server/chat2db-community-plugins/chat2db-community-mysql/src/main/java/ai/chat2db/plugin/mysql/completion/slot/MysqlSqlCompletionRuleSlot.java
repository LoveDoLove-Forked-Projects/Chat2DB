package ai.chat2db.plugin.mysql.completion.slot;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.enums.completion.MysqlSqlCompletionRuleSlotTypeEnum;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.antlr.v4.runtime.Token;


public record MysqlSqlCompletionRuleSlot(MysqlSqlCompletionRuleSlotTypeEnum type) {

    private static final Comparator<SlotCandidate> SLOT_ORDERING =
            Comparator.comparing(SlotCandidate::orderingKey);

    public MysqlSqlCompletionRuleSlot {
        type = type == null ? MysqlSqlCompletionRuleSlotTypeEnum.UNKNOWN : type;
    }

    public static MysqlSqlCompletionRuleSlot classify(MysqlSqlCompletionCandidateContext context,
                                                           SqlCompletionCandidates c3Result) {
        return new MysqlSqlCompletionRuleSlot(classifyByRules(context, c3Result));
    }

    public boolean tableReference() {
        return type == MysqlSqlCompletionRuleSlotTypeEnum.TABLE_REFERENCE;
    }

    public SqlCompletionCandidateTypeEnum metadataType() {
        return switch (type) {
            case DATABASE_REFERENCE -> SqlCompletionCandidateTypeEnum.DATABASE;
            case EVENT_REFERENCE -> SqlCompletionCandidateTypeEnum.EVENT;
            case FUNCTION_REFERENCE -> SqlCompletionCandidateTypeEnum.FUNCTION;
            case PROCEDURE_REFERENCE -> SqlCompletionCandidateTypeEnum.PROCEDURE;
            case ROLE_REFERENCE -> SqlCompletionCandidateTypeEnum.ROLE;
            case TABLESPACE_REFERENCE -> SqlCompletionCandidateTypeEnum.TABLESPACE;
            case TRIGGER_REFERENCE -> SqlCompletionCandidateTypeEnum.TRIGGER;
            case USER_REFERENCE -> SqlCompletionCandidateTypeEnum.USER;
            default -> null;
        };
    }

    public boolean objectReference() {
        return metadataType() != null;
    }

    public boolean columnReference() {
        return type == MysqlSqlCompletionRuleSlotTypeEnum.COLUMN_REFERENCE;
    }

    public boolean dataTypeReference() {
        return type == MysqlSqlCompletionRuleSlotTypeEnum.DATA_TYPE_REFERENCE;
    }

    public boolean charsetReference() {
        return type == MysqlSqlCompletionRuleSlotTypeEnum.CHARSET_REFERENCE;
    }

    public boolean collationReference() {
        return type == MysqlSqlCompletionRuleSlotTypeEnum.COLLATION_REFERENCE;
    }

    public boolean insertValueExpression() {
        return type == MysqlSqlCompletionRuleSlotTypeEnum.INSERT_VALUE_EXPRESSION;
    }

    public boolean tableDeclaration() {
        return type == MysqlSqlCompletionRuleSlotTypeEnum.TABLE_DECLARATION;
    }

    public static MysqlSqlCompletionRuleSlot unknown() {
        return new MysqlSqlCompletionRuleSlot(MysqlSqlCompletionRuleSlotTypeEnum.UNKNOWN);
    }

    private static MysqlSqlCompletionRuleSlotTypeEnum classifyByRules(MysqlSqlCompletionCandidateContext context,
                                                                    SqlCompletionCandidates c3Result) {
        if (userVariablePrefix(context)) {
            return MysqlSqlCompletionRuleSlotTypeEnum.ROUTINE_LOCAL_SYMBOL;
        }
        if (c3Result == null || !c3Result.available()) {
            return MysqlSqlCompletionRuleSlotTypeEnum.UNKNOWN;
        }
        int tokenIndex = c3Result.tokenIndex();
        List<SlotCandidate> candidates = c3Result.rules().entrySet().stream()
                .map(entry -> toSlot(context, entry, tokenIndex))
                .filter(Objects::nonNull)
                .filter(candidate -> candidate.covers(tokenIndex))
                .toList();
        return candidates.stream()
                .max(SLOT_ORDERING)
                .map(SlotCandidate::type)
                .orElse(MysqlSqlCompletionRuleSlotTypeEnum.UNKNOWN);
    }

    private static boolean userVariablePrefix(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return false;
        }
        String sourceSql = context.window().sourceSql();
        int replaceStart = Math.max(0, Math.min(context.cursorContext().replaceStart(), sourceSql.length()));
        if (replaceStart > 0 && sourceSql.charAt(replaceStart - 1) == '@') {
            return true;
        }
        List<Token> tokens = MysqlSqlCompletionTokenUtil.tokens(context.window().parseSql());
        Token token = MysqlSqlCompletionTokenUtil.tokenCoveringRange(tokens, replaceStart,
                Math.max(replaceStart, context.window().cursor()));
        return token != null && token.getType() == MySqlLexer.LOCAL_ID;
    }

    private static SlotCandidate toSlot(MysqlSqlCompletionCandidateContext context,
                                        Map.Entry<Integer, SqlCompletionCandidates.RuleCandidate> entry,
                                        int tokenIndex) {
        if (entry.getValue() == null) {
            return null;
        }
        MysqlSqlCompletionRuleSlotTypeEnum type = MysqlSqlCompletionRuleSlotTypeEnum.fromRule(entry.getKey());
        if (type == MysqlSqlCompletionRuleSlotTypeEnum.UNKNOWN) {
            type = MysqlSqlCompletionRuleSlotTypeEnum.fromRulePath(entry.getValue().ruleList());
        }
        if (type == MysqlSqlCompletionRuleSlotTypeEnum.UNKNOWN) {
            return null;
        }
        if (type == MysqlSqlCompletionRuleSlotTypeEnum.DATA_TYPE_REFERENCE
                && entry.getValue().startTokenIndex() != tokenIndex) {
            return null;
        }
        if (type == MysqlSqlCompletionRuleSlotTypeEnum.CHARSET_REFERENCE
                && entry.getValue().startTokenIndex() != tokenIndex) {
            return null;
        }
        if (type == MysqlSqlCompletionRuleSlotTypeEnum.COLLATION_REFERENCE
                && entry.getValue().startTokenIndex() != tokenIndex) {
            return null;
        }
        if (type == MysqlSqlCompletionRuleSlotTypeEnum.ROUTINE_LOCAL_SYMBOL
                && !routineBody(entry.getValue().ruleList())) {
            return null;
        }
        if (type == MysqlSqlCompletionRuleSlotTypeEnum.ALIAS_DECLARATION
                && (dotScoped(context)
                || !selectElementAlias(entry.getValue().ruleList())
                || routineBody(entry.getValue().ruleList()))) {
            return null;
        }
        return new SlotCandidate(type, entry.getValue().startTokenIndex(), entry.getValue().stopTokenIndex(),
                entry.getValue().ruleList().size());
    }

    private static boolean dotScoped(MysqlSqlCompletionCandidateContext context) {
        return context != null && context.cursorContext() != null && context.cursorContext().dotScoped();
    }

    private static boolean selectElementAlias(List<Integer> ruleList) {
        return ruleList != null
                && ruleList.contains(MySqlParser.RULE_selectElements)
                && ruleList.contains(MySqlParser.RULE_selectElement);
    }

    private static boolean routineBody(List<Integer> ruleList) {
        return ruleList != null && ruleList.contains(MySqlParser.RULE_routineBody);
    }

    private record SlotCandidate(MysqlSqlCompletionRuleSlotTypeEnum type,
                                 int startTokenIndex,
                                 int stopTokenIndex,
                                 int ruleDepth) {

        private boolean covers(int tokenIndex) {
            return startTokenIndex <= tokenIndex && tokenIndex <= stopTokenIndex;
        }

        private SlotOrderingKey orderingKey() {
            return new SlotOrderingKey(startTokenIndex, ruleDepth, type.name());
        }
    }

    private record SlotOrderingKey(int startTokenIndex,
                                   int ruleDepth,
                                   String stableTieBreakKey) implements Comparable<SlotOrderingKey> {

        @Override
        public int compareTo(SlotOrderingKey other) {
            if (other == null) {
                return 1;
            }
            int byStart = Integer.compare(startTokenIndex, other.startTokenIndex);
            if (byStart != 0) {
                return byStart;
            }
            int byDepth = Integer.compare(ruleDepth, other.ruleDepth);
            if (byDepth != 0) {
                return byDepth;
            }
            return stableTieBreakKey.compareTo(other.stableTieBreakKey);
        }
    }
}
