package ai.chat2db.plugin.mysql.completion.context;

import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.plugin.mysql.completion.resolver.MysqlSqlCompletionRelationScopeResolver;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.service.db.ISqlCompletionMetadataProvider;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalColumn;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalContext;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalRelation;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalVariable;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionSourceSpan;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionLocalSymbolSourceTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionParameterModeTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import ai.chat2db.spi.ISqlCompletionLocalContextCollector;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionLocalContextCollector implements ISqlCompletionLocalContextCollector {

    private static final Set<Integer> ROUTINE_START_TOKENS = Set.of(
            MySqlLexer.PROCEDURE,
            MySqlLexer.FUNCTION);

    private static final Set<Integer> PARAMETER_DIRECTION_TOKENS = Set.of(
            MySqlLexer.IN,
            MySqlLexer.OUT,
            MySqlLexer.INOUT);

    private static final Set<Integer> PARAMETER_LIST_STOP_TOKENS = Set.of(
            MySqlLexer.RR_BRACKET,
            MySqlLexer.RETURNS,
            MySqlLexer.BEGIN);

    private static final Set<Integer> CREATE_TABLE_COLUMN_STOP_TOKENS = Set.of(
            MySqlLexer.PRIMARY,
            MySqlLexer.UNIQUE,
            MySqlLexer.KEY,
            MySqlLexer.INDEX,
            MySqlLexer.CONSTRAINT,
            MySqlLexer.FOREIGN,
            MySqlLexer.CHECK,
            MySqlLexer.FULLTEXT,
            MySqlLexer.SPATIAL);

    @Override
    public SqlCompletionLocalContext collect(SqlCompletionPipelineState state) {
        if (state == null || state.window() == null || state.cursorContext() == null
                || !state.cursorContext().admitted()) {
            return SqlCompletionLocalContext.empty();
        }
        String sql = editorParseSql(state);
        int cursor = Math.max(0, Math.min(state.request().cursor(), sql.length()));
        List<Token> tokens = MysqlSqlCompletionTokenUtil.significantDefaultTokens(sql);
        Map<String, SqlCompletionLocalRelation> relations = new LinkedHashMap<>();
        collectCurrentStatementRelations(state, relations);
        collectDraftTables(tokens, cursor, relations);
        Map<String, SqlCompletionLocalVariable> variables = new LinkedHashMap<>();
        collectRoutineVariables(tokens, cursor, variables);
        collectUserVariables(state, tokens, cursor, variables);
        return new SqlCompletionLocalContext(new ArrayList<>(relations.values()), new ArrayList<>(variables.values()));
    }

    private static String editorParseSql(SqlCompletionPipelineState state) {
        if (state.input() != null && state.input().parseSql() != null) {
            return state.input().parseSql();
        }
        return state.window().parseSql();
    }

    private static void collectCurrentStatementRelations(SqlCompletionPipelineState state,
                                                         Map<String, SqlCompletionLocalRelation> relations) {
        MysqlSqlCompletionRelationScope relationScope = MysqlSqlCompletionRelationScopeResolver.resolve(
                state.window(), state.cursorContext());
        for (MysqlSqlCompletionRelationScope.Relation relation : relationScope.relations()) {
            if (StringUtils.isBlank(relation.table())) {
                continue;
            }
            List<SqlCompletionLocalColumn> columns = relation.columns().stream()
                    .filter(StringUtils::isNotBlank)
                    .map(column -> new SqlCompletionLocalColumn(column, null, null))
                    .toList();
            addRelation(relations, new SqlCompletionLocalRelation(relation.catalog(), relation.schema(),
                    relation.table(), relation.alias(), columns,
                    SqlCompletionLocalSymbolSourceTypeEnum.CURRENT_STATEMENT.name(), null));
        }
    }

    private static void collectDraftTables(List<Token> tokens,
                                           int cursor,
                                           Map<String, SqlCompletionLocalRelation> relations) {
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() != MySqlLexer.CREATE) {
                continue;
            }
            int tableKeywordIndex = createTableKeywordIndex(tokens, index + 1);
            if (tableKeywordIndex < 0 || tableKeywordIndex >= tokens.size()
                    || tokens.get(tableKeywordIndex).getType() != MySqlLexer.TABLE) {
                continue;
            }
            DraftTable draftTable = draftTable(tokens, tableKeywordIndex + 1, cursor);
            if (draftTable != null) {
                addRelation(relations, draftTable.relation());
                index = Math.max(index, draftTable.endIndex());
            }
        }
    }

    private static int createTableKeywordIndex(List<Token> tokens, int startIndex) {
        int index = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, startIndex);
        if (index >= 0 && index < tokens.size() && tokens.get(index).getType() == MySqlLexer.TEMPORARY) {
            index = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, index + 1);
        }
        return index;
    }

    private static DraftTable draftTable(List<Token> tokens, int startIndex, int cursor) {
        int nameIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, startIndex);
        if (nameIndex >= 0 && tokens.get(nameIndex).getType() == MySqlLexer.IF) {
            nameIndex = skipIfNotExists(tokens, nameIndex);
            if (nameIndex < 0) {
                return null;
            }
        }
        if (nameIndex < 0 || nameIndex >= tokens.size()) {
            return null;
        }
        int nameEndIndex = qualifiedIdentifierEndIndex(tokens, nameIndex);
        if (nameEndIndex < nameIndex || !MysqlSqlCompletionTokenUtil.tokenEndsAtOrBefore(tokens.get(nameEndIndex),
                cursor)) {
            return null;
        }
        QualifiedName name = qualifiedName(tokens, nameIndex, nameEndIndex);
        if (StringUtils.isBlank(name.table())) {
            return null;
        }
        int openIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, nameEndIndex + 1);
        if (openIndex < 0 || openIndex >= tokens.size() || tokens.get(openIndex).getType() != MySqlLexer.LR_BRACKET) {
            return new DraftTable(new SqlCompletionLocalRelation(name.catalog(), name.schema(), name.table(), null,
                    List.of(), SqlCompletionLocalSymbolSourceTypeEnum.DRAFT_DDL.name(),
                    SqlCompletionSourceSpan.of(tokens.get(nameIndex).getStartIndex(),
                            tokens.get(nameEndIndex).getStopIndex() + 1)), nameEndIndex);
        }
        int closeIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, openIndex);
        int endIndex = closeIndex < 0 ? statementEndIndex(tokens, openIndex + 1) : closeIndex;
        List<SqlCompletionLocalColumn> columns = createTableColumns(tokens, openIndex + 1, endIndex);
        int spanEndIndex = Math.min(tokens.size() - 1, Math.max(nameEndIndex, endIndex));
        return new DraftTable(new SqlCompletionLocalRelation(name.catalog(), name.schema(), name.table(), null,
                columns, SqlCompletionLocalSymbolSourceTypeEnum.DRAFT_DDL.name(),
                SqlCompletionSourceSpan.of(tokens.get(nameIndex).getStartIndex(),
                        tokens.get(spanEndIndex).getStopIndex() + 1)), endIndex);
    }

    private static int skipIfNotExists(List<Token> tokens, int ifIndex) {
        int notIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, ifIndex + 1);
        int existsIndex = notIndex < 0 ? -1 : MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, notIndex + 1);
        if (notIndex >= 0 && existsIndex >= 0
                && tokens.get(notIndex).getType() == MySqlLexer.NOT
                && tokens.get(existsIndex).getType() == MySqlLexer.EXISTS) {
            return MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, existsIndex + 1);
        }
        return -1;
    }

    private static int statementEndIndex(List<Token> tokens, int startIndex) {
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            if (tokens.get(index).getType() == MySqlLexer.SEMI) {
                return index;
            }
        }
        return tokens.size();
    }

    private static List<SqlCompletionLocalColumn> createTableColumns(List<Token> tokens, int start, int endExclusive) {
        List<SqlCompletionLocalColumn> columns = new ArrayList<>();
        int segmentStart = Math.max(0, start);
        int depth = 0;
        for (int index = Math.max(0, start); index < Math.min(endExclusive, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
                continue;
            }
            if (depth == 0 && token.getType() == MySqlLexer.COMMA) {
                addCreateTableColumn(tokens, segmentStart, index, columns);
                segmentStart = index + 1;
            }
        }
        addCreateTableColumn(tokens, segmentStart, endExclusive, columns);
        return columns;
    }

    private static void addCreateTableColumn(List<Token> tokens,
                                             int start,
                                             int endExclusive,
                                             List<SqlCompletionLocalColumn> columns) {
        int nameIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, start);
        if (nameIndex < 0 || nameIndex >= endExclusive) {
            return;
        }
        Token nameToken = tokens.get(nameIndex);
        if (!MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(nameToken)
                || CREATE_TABLE_COLUMN_STOP_TOKENS.contains(nameToken.getType())
                || MysqlSqlCompletionTokenUtil.isCompletionDummy(nameToken)) {
            return;
        }
        String name = MysqlSqlCompletionTokenUtil.identifierText(nameToken);
        if (StringUtils.isBlank(name)) {
            return;
        }
        columns.add(new SqlCompletionLocalColumn(name, dataTypeAfter(tokens, nameIndex + 1, endExclusive),
                SqlCompletionSourceSpan.of(nameToken.getStartIndex(), nameToken.getStopIndex() + 1)));
    }

    private static String dataTypeAfter(List<Token> tokens, int start, int endExclusive) {
        for (int index = Math.max(0, start); index < Math.min(endExclusive, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (MysqlSqlCompletionTokenUtil.isDataTypeNameToken(token)) {
                return tokenText(token);
            }
        }
        return null;
    }

    private static void collectRoutineVariables(List<Token> tokens,
                                                int cursor,
                                                Map<String, SqlCompletionLocalVariable> variables) {
        int routineIndex = activeRoutineIndex(tokens, cursor);
        if (routineIndex < 0) {
            return;
        }
        collectRoutineParameters(tokens, routineIndex, cursor, variables);
        collectDeclaredVariables(tokens, routineIndex + 1, cursor, variables);
    }

    private static void collectRoutineParameters(List<Token> tokens,
                                                 int routineIndex,
                                                 int cursor,
                                                 Map<String, SqlCompletionLocalVariable> variables) {
        int openIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, routineIndex + 1, cursor,
                MySqlLexer.LR_BRACKET);
        if (openIndex < 0) {
            return;
        }
        int closeIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, openIndex);
        if (closeIndex < 0 || !MysqlSqlCompletionTokenUtil.tokenEndsAtOrBefore(tokens.get(closeIndex), cursor)) {
            return;
        }
        int endIndex = closeIndex < 0 ? tokens.size() : closeIndex;
        int segmentStart = openIndex + 1;
        int depth = 0;
        for (int index = openIndex + 1; index < Math.min(endIndex, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                if (depth == 0) {
                    break;
                }
                depth--;
                continue;
            }
            if (depth == 0 && token.getType() == MySqlLexer.COMMA) {
                addRoutineParameter(tokens, segmentStart, index, variables);
                segmentStart = index + 1;
            }
        }
        addRoutineParameter(tokens, segmentStart, endIndex, variables);
    }

    private static void collectDeclaredVariables(List<Token> tokens,
                                                 int startIndex,
                                                 int cursor,
                                                 Map<String, SqlCompletionLocalVariable> variables) {
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() != MySqlLexer.DECLARE) {
                continue;
            }
            int segmentEnd = declarationEnd(tokens, index + 1, cursor);
            if (segmentEnd < 0) {
                continue;
            }
            addDeclareVariables(tokens, index + 1, segmentEnd, variables);
        }
    }

    private static void addRoutineParameter(List<Token> tokens,
                                            int startIndex,
                                            int endExclusive,
                                            Map<String, SqlCompletionLocalVariable> variables) {
        int index = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, startIndex);
        if (index < 0 || index >= endExclusive) {
            return;
        }
        Token token = tokens.get(index);
        if (PARAMETER_DIRECTION_TOKENS.contains(token.getType())) {
            index = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, index + 1);
            if (index < 0 || index >= endExclusive) {
                return;
            }
            token = tokens.get(index);
        }
        if (!MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(token)) {
            return;
        }
        addRoutineVariable(token, dataTypeAfter(tokens, index + 1, endExclusive), variables);
    }

    private static void addDeclareVariables(List<Token> tokens,
                                            int startIndex,
                                            int endExclusive,
                                            Map<String, SqlCompletionLocalVariable> variables) {
        List<Token> names = new ArrayList<>();
        int typeStartIndex = -1;
        for (int index = Math.max(0, startIndex); index < Math.min(endExclusive, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (MysqlSqlCompletionTokenUtil.isDataTypeNameToken(token)) {
                typeStartIndex = index;
                break;
            }
            if (token.getType() == MySqlLexer.COMMA) {
                continue;
            }
            if (!MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(token)) {
                return;
            }
            names.add(token);
        }
        String dataType = typeStartIndex < 0 ? null : tokenText(tokens.get(typeStartIndex));
        for (Token name : names) {
            addRoutineVariable(name, dataType, variables);
        }
    }

    private static void addRoutineVariable(Token token,
                                           String dataType,
                                           Map<String, SqlCompletionLocalVariable> variables) {
        String name = tokenText(token);
        if (StringUtils.isBlank(name) || MysqlSqlCompletionTokenUtil.isCompletionDummy(token)) {
            return;
        }
        variables.putIfAbsent(normalize(name), new SqlCompletionLocalVariable(name, dataType,
                SqlCompletionLocalSymbolSourceTypeEnum.ROUTINE_LOCAL.name(),
                SqlCompletionSourceSpan.of(token.getStartIndex(), token.getStopIndex() + 1)));
    }

    private static void collectUserVariables(SqlCompletionPipelineState state,
                                             List<Token> tokens,
                                             int cursor,
                                             Map<String, SqlCompletionLocalVariable> variables) {
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() != MySqlLexer.LOCAL_ID) {
                continue;
            }
            String name = tokenText(token);
            UserVariableEvidence evidence = userVariableEvidence(state, tokens, index);
            if (StringUtils.isBlank(name) || !evidence.collect()) {
                continue;
            }
            variables.putIfAbsent(normalize(name), new SqlCompletionLocalVariable(name, evidence.dataType(),
                    SqlCompletionLocalSymbolSourceTypeEnum.USER_VARIABLE.name(),
                    SqlCompletionSourceSpan.of(token.getStartIndex(), token.getStopIndex() + 1)));
        }
    }

    private static UserVariableEvidence userVariableEvidence(SqlCompletionPipelineState state,
                                                             List<Token> tokens,
                                                             int index) {
        int previousIndex = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, index - 1);
        int nextIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, index + 1);
        if (nextIndex >= 0) {
            int nextType = tokens.get(nextIndex).getType();
            if (nextType == MySqlLexer.VAR_ASSIGN
                    || (nextType == MySqlLexer.EQUAL_SYMBOL && setStatementVariableTarget(tokens, index))) {
                return UserVariableEvidence.collect(null);
            }
        }
        if (previousIndex >= 0) {
            int previousType = tokens.get(previousIndex).getType();
            if (previousType == MySqlLexer.COMMA || previousType == MySqlLexer.LR_BRACKET) {
                UserVariableEvidence callEvidence = callArgumentVariable(state, tokens, previousIndex, index);
                if (callEvidence.collect()) {
                    return callEvidence;
                }
                if (previousType == MySqlLexer.COMMA && intoVariableContinuation(tokens, index)) {
                    return UserVariableEvidence.collect(null);
                }
                return UserVariableEvidence.ignore();
            }
            if (previousType == MySqlLexer.INTO) {
                return UserVariableEvidence.collect(null);
            }
        }
        return UserVariableEvidence.ignore();
    }

    private static boolean setStatementVariableTarget(List<Token> tokens, int variableIndex) {
        int statementStart = 0;
        for (int index = variableIndex - 1; index >= 0; index--) {
            if (tokens.get(index).getType() == MySqlLexer.SEMI) {
                statementStart = index + 1;
                break;
            }
        }
        int firstIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, statementStart);
        return firstIndex >= 0 && firstIndex < variableIndex && tokens.get(firstIndex).getType() == MySqlLexer.SET;
    }

    private static boolean intoVariableContinuation(List<Token> tokens, int variableIndex) {
        int depth = 0;
        for (int index = variableIndex - 1; index >= 0; index--) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.SEMI && depth == 0) {
                return false;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                if (depth > 0) {
                    depth--;
                    continue;
                }
                return false;
            }
            if (depth > 0) {
                continue;
            }
            if (token.getType() == MySqlLexer.INTO) {
                return true;
            }
            if (token.getType() == MySqlLexer.COMMA || token.getType() == MySqlLexer.LOCAL_ID) {
                continue;
            }
            return false;
        }
        return false;
    }

    private static UserVariableEvidence callArgumentVariable(SqlCompletionPipelineState state,
                                                            List<Token> tokens,
                                                            int previousIndex,
                                                            int variableIndex) {
        int depth = 0;
        for (int index = previousIndex; index >= 0; index--) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                if (depth == 0) {
                    int callableIndex = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, index - 1);
                    int callableStartIndex = qualifiedIdentifierStartIndex(tokens, callableIndex);
                    int callIndex = callableStartIndex < 0 ? -1 : MysqlSqlCompletionTokenUtil.previousDefaultIndex(
                            tokens, callableStartIndex - 1);
                    if (callIndex < 0 || tokens.get(callIndex).getType() != MySqlLexer.CALL) {
                        return UserVariableEvidence.ignore();
                    }
                    QualifiedName callable = qualifiedName(tokens, callableStartIndex, callableIndex);
                    int argumentOrdinal = callArgumentOrdinal(tokens, index + 1, variableIndex);
                    return routineOutputParameter(state, callable, argumentOrdinal);
                }
                depth--;
            }
        }
        return UserVariableEvidence.ignore();
    }

    private static int callArgumentOrdinal(List<Token> tokens, int startIndex, int variableIndex) {
        int ordinal = 1;
        int depth = 0;
        for (int index = Math.max(0, startIndex); index < Math.min(variableIndex, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
                continue;
            }
            if (depth == 0 && token.getType() == MySqlLexer.COMMA) {
                ordinal++;
            }
        }
        return ordinal;
    }

    private static UserVariableEvidence routineOutputParameter(SqlCompletionPipelineState state,
                                                               QualifiedName callable,
                                                               int argumentOrdinal) {
        if (state == null || state.request() == null || state.request().metadataProvider() == null
                || callable == null || StringUtils.isBlank(callable.table()) || argumentOrdinal < 1) {
            return UserVariableEvidence.ignore();
        }
        ISqlCompletionMetadataProvider metadataProvider = state.request().metadataProvider();
        SqlCompletionMetadataScope scope = new SqlCompletionMetadataScope(callable.catalog(), callable.schema(),
                null, callable.table());
        SqlCompletionMetadataResponse result = metadataProvider.list(DbSqlCompletionMetadataRequest.of(
                SqlCompletionCandidateTypeEnum.PARAMETER, scope, "", SqlCompletionCandidateTypeEnum.PROCEDURE));
        if (result == null || !SqlCompletionStatusEnum.SUCCESS.name().equals(result.getStatus())
                || result.getCandidates() == null || result.getCandidates().isEmpty()) {
            return UserVariableEvidence.ignore();
        }
        List<SqlCompletionCandidate> candidates = result.getCandidates();
        for (int index = 0; index < candidates.size(); index++) {
            SqlCompletionCandidate candidate = candidates.get(index);
            if (candidate == null) {
                continue;
            }
            int ordinal = candidate.getSortRank() == null ? index + 1 : candidate.getSortRank();
            if (ordinal == argumentOrdinal && outputParameter(candidate.getParameterMode())) {
                return UserVariableEvidence.collect(candidate.getDataType());
            }
        }
        return UserVariableEvidence.ignore();
    }

    private static boolean outputParameter(SqlCompletionParameterModeTypeEnum parameterMode) {
        return parameterMode != null && parameterMode.outputArgument();
    }

    private static int activeRoutineIndex(List<Token> tokens, int cursor) {
        int result = -1;
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (ROUTINE_START_TOKENS.contains(token.getType())) {
                result = index;
            }
        }
        if (result < 0 || !routineParameterListClosedBeforeCursor(tokens, result, cursor)) {
            return -1;
        }
        int beginIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, result + 1, cursor,
                MySqlLexer.BEGIN);
        if (beginIndex >= 0) {
            return result;
        }
        int returnsIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, result + 1, cursor,
                MySqlLexer.RETURNS);
        return returnsIndex >= 0 ? result : -1;
    }

    private static boolean routineParameterListClosedBeforeCursor(List<Token> tokens, int routineIndex, int cursor) {
        int openIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, routineIndex + 1, cursor,
                MySqlLexer.LR_BRACKET);
        if (openIndex < 0) {
            return false;
        }
        int closeIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, openIndex);
        return closeIndex >= 0 && MysqlSqlCompletionTokenUtil.tokenEndsAtOrBefore(tokens.get(closeIndex), cursor);
    }

    private static int declarationEnd(List<Token> tokens, int startIndex, int cursor) {
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == MySqlLexer.SEMI) {
                return index;
            }
        }
        return -1;
    }

    private static int qualifiedIdentifierEndIndex(List<Token> tokens, int start) {
        if (start < 0 || start >= tokens.size()) {
            return -1;
        }
        int end = start;
        int index = start + 1;
        while (index < tokens.size()) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.DOT_ID) {
                end = index;
                index++;
                continue;
            }
            if (token.getType() == MySqlLexer.DOT
                    && index + 1 < tokens.size()
                    && MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(tokens.get(index + 1))) {
                end = index + 1;
                index += 2;
                continue;
            }
            break;
        }
        return end;
    }

    private static int qualifiedIdentifierStartIndex(List<Token> tokens, int end) {
        if (end < 0 || end >= tokens.size()) {
            return -1;
        }
        if (!MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(tokens.get(end))
                && tokens.get(end).getType() != MySqlLexer.DOT_ID) {
            return -1;
        }
        int start = end;
        while (true) {
            int previous = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, start - 1);
            if (previous < 0) {
                break;
            }
            Token previousToken = tokens.get(previous);
            Token startToken = tokens.get(start);
            if (startToken.getType() == MySqlLexer.DOT_ID
                    && (previousToken.getType() == MySqlLexer.DOT_ID
                    || MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(previousToken))) {
                start = previous;
                continue;
            }
            if (previousToken.getType() == MySqlLexer.DOT) {
                int ownerIndex = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, previous - 1);
                if (ownerIndex >= 0
                        && (tokens.get(ownerIndex).getType() == MySqlLexer.DOT_ID
                        || MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(tokens.get(ownerIndex)))) {
                    start = ownerIndex;
                    continue;
                }
            }
            break;
        }
        return start;
    }

    private static QualifiedName qualifiedName(List<Token> tokens, int start, int end) {
        List<String> parts = new ArrayList<>();
        for (int index = start; index <= end && index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.DOT) {
                continue;
            }
            if (token.getType() == MySqlLexer.DOT_ID) {
                parts.add(tokenText(MysqlSqlCompletionTokenUtil.stripLeadingDot(token.getText())));
                continue;
            }
            if (MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(token)) {
                parts.add(tokenText(token));
            }
        }
        if (parts.size() >= 3) {
            return new QualifiedName(parts.get(parts.size() - 3), parts.get(parts.size() - 2),
                    parts.get(parts.size() - 1));
        }
        if (parts.size() == 2) {
            return new QualifiedName(null, parts.get(0), parts.get(1));
        }
        return new QualifiedName(null, null, parts.isEmpty() ? "" : parts.get(0));
    }

    private static void addRelation(Map<String, SqlCompletionLocalRelation> relations,
                                    SqlCompletionLocalRelation relation) {
        if (relation == null || StringUtils.isBlank(relation.name())) {
            return;
        }
        relations.merge(relationKey(relation), relation, MysqlSqlCompletionLocalContextCollector::mergeRelation);
    }

    private static SqlCompletionLocalRelation mergeRelation(SqlCompletionLocalRelation existing,
                                                            SqlCompletionLocalRelation incoming) {
        if (existing == null) {
            return incoming;
        }
        if (incoming == null) {
            return existing;
        }
        List<SqlCompletionLocalColumn> columns = existing.columns().isEmpty()
                ? incoming.columns()
                : existing.columns();
        SqlCompletionSourceSpan sourceSpan = existing.sourceSpan() == null ? incoming.sourceSpan()
                : existing.sourceSpan();
        return new SqlCompletionLocalRelation(existing.catalog(), existing.schema(), existing.name(),
                existing.alias(), columns, existing.sourceType(), sourceSpan);
    }

    private static String relationKey(SqlCompletionLocalRelation relation) {
        return String.join("|", normalize(relation.catalog()), normalize(relation.schema()), normalize(relation.name()),
                normalize(relation.alias()));
    }

    private static String normalize(String value) {
        return StringUtils.defaultString(value).toLowerCase();
    }

    private static String tokenText(Token token) {
        return tokenText(token == null ? "" : token.getText());
    }

    private static String tokenText(String value) {
        return MysqlSqlCompletionTokenUtil.stripIdentifierQuotes(
                MysqlSqlCompletionTokenUtil.stripLeadingDot(value));
    }

    private record DraftTable(SqlCompletionLocalRelation relation, int endIndex) {
    }

    private record QualifiedName(String catalog, String schema, String table) {
    }

    private record UserVariableEvidence(boolean collect, String dataType) {

        private static UserVariableEvidence collect(String dataType) {
            return new UserVariableEvidence(true, dataType);
        }

        private static UserVariableEvidence ignore() {
            return new UserVariableEvidence(false, null);
        }
    }
}
