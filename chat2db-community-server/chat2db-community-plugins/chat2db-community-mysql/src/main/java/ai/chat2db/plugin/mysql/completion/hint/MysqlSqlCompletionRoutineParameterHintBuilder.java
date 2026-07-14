package ai.chat2db.plugin.mysql.completion.hint;

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


import static ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionRoutineParameterHintBuilderConstants.*;
final class MysqlSqlCompletionRoutineParameterHintBuilder {


    private static final Set<Integer> DECLARATION_NAME_INTRODUCERS = Set.of(
            MySqlLexer.TABLE,
            MySqlLexer.VIEW,
            MySqlLexer.FUNCTION,
            MySqlLexer.PROCEDURE,
            MySqlLexer.EVENT,
            MySqlLexer.TRIGGER,
            MySqlLexer.INDEX,
            MySqlLexer.KEY,
            MySqlLexer.CONSTRAINT);

    List<SqlCompletionEditorHint> build(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.metadataProvider() == null) {
            return List.of();
        }
        String sql = Objects.toString(context.window().parseSql(), "");
        int cursor = Math.max(0, Math.min(context.window().cursor(), sql.length()));
        int sourceOffset = context.window().sourceStartOffset();
        String sourceSql = Objects.toString(context.input().sourceSql(), "");
        int sourceCursor = Math.max(0, Math.min(context.window().sourceCursor(), sourceSql.length()));
        if (dataTypeContext(context)
                || MysqlSqlCompletionTokenUtil.cursorInsideRawLiteralOrComment(sourceSql, sourceCursor)
                || MysqlSqlCompletionTokenUtil.cursorInsideRawLiteralOrComment(sql, cursor)) {
            return List.of();
        }

        List<Token> tokens = MysqlSqlCompletionTokenUtil.significantDefaultTokens(sql);
        return callWindows(tokens, cursor).stream()
                .limit(MAX_ROUTINE_PARAMETER_HINT_LOOKUPS)
                .map(callWindow -> buildHint(context, sourceSql, sourceOffset, sql.length(), tokens, callWindow, cursor))
                .filter(Objects::nonNull)
                .toList();
    }

    private SqlCompletionEditorHint buildHint(MysqlSqlCompletionCandidateContext context,
                                              String sourceSql,
                                              int sourceOffset,
                                              int sqlLength,
                                              List<Token> tokens,
                                              CallWindow callWindow,
                                              int cursor) {
        List<SqlCompletionCandidate> parameters = routineParameters(context, callWindow);
        if (parameters.isEmpty()) {
            return null;
        }
        boolean activeCall = cursor >= callWindow.openOffset()
                && cursor <= Math.max(callWindow.openOffset(), callWindow.closeOffset());
        int activeIndex = activeCall ? activeArgumentIndex(tokens, callWindow, cursor) : -1;
        List<ValueRange> argumentRanges = argumentRanges(tokens, callWindow);
        ValueRange activeRange = valueRangeAt(argumentRanges, activeIndex, callWindow, cursor);
        SqlCompletionEditorHint hint = new SqlCompletionEditorHint();
        hint.setType(SqlCompletionEditorHintTypeEnum.ROUTINE_PARAMETER);
        hint.setStatementRange(range(sourceSql, sourceOffset, 0, sqlLength));
        hint.setRowRange(range(sourceSql, sourceOffset, callWindow.openOffset(), callWindow.closeOffset()));
        if (activeCall) {
            hint.setValueRange(range(sourceSql, sourceOffset, activeRange.startOffset(), activeRange.endOffset()));
        }
        hint.setItems(items(sourceSql, sourceOffset, parameters, argumentRanges, activeIndex, activeRange));
        return hint.getItems().isEmpty() ? null : hint;
    }

    private List<SqlCompletionCandidate> routineParameters(MysqlSqlCompletionCandidateContext context,
                                                            CallWindow callWindow) {
        List<String> name = callWindow.nameParts();
        if (name.isEmpty() || name.size() > 3) {
            return List.of();
        }
        String databaseName = null;
        String schemaName = null;
        String routineName = name.get(name.size() - 1);
        if (name.size() == 2) {
            databaseName = name.get(0);
        } else if (name.size() == 3) {
            databaseName = name.get(0);
            schemaName = name.get(1);
        }
        SqlCompletionMetadataResponse result = context.metadataProvider().list(DbSqlCompletionMetadataRequest.of(
                SqlCompletionCandidateTypeEnum.PARAMETER,
                new SqlCompletionMetadataScope(databaseName, schemaName, null, routineName),
                "",
                callWindow.procedure()
                        ? SqlCompletionCandidateTypeEnum.PROCEDURE
                        : SqlCompletionCandidateTypeEnum.FUNCTION));
        if (result == null || !SqlCompletionStatusEnum.SUCCESS.name().equals(result.getStatus()) || result.getCandidates() == null) {
            return List.of();
        }
        return result.getCandidates().stream()
                .filter(Objects::nonNull)
                .filter(candidate -> StringUtils.isNotBlank(parameterName(candidate)))
                .sorted(Comparator.comparingInt(candidate -> candidate.getSortRank() == null
                        ? Integer.MAX_VALUE
                        : candidate.getSortRank()))
                .toList();
    }

    private List<SqlCompletionEditorHint.Item> items(String sourceSql,
                                                     int sourceOffset,
                                                     List<SqlCompletionCandidate> parameters,
                                                     List<ValueRange> argumentRanges,
                                                     int activeIndex,
                                                     ValueRange activeRange) {
        List<SqlCompletionEditorHint.Item> items = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            SqlCompletionCandidate parameter = parameters.get(i);
            String name = parameterName(parameter);
            if (StringUtils.isBlank(name)) {
                continue;
            }
            String type = parameterType(parameter);
            ValueRange itemRange = i < argumentRanges.size()
                    ? argumentRanges.get(i)
                    : new ValueRange(activeRange.startOffset(), activeRange.startOffset());
            SqlCompletionEditorHint.Item item = new SqlCompletionEditorHint.Item();
            item.setRowIndex(0);
            item.setColumnIndex(i);
            item.setFieldName(name);
            item.setFieldType(type);
            item.setLabel(StringUtils.isBlank(type) ? name : name + ":" + type);
            item.setRange(range(sourceSql, sourceOffset, itemRange.startOffset(), itemRange.endOffset()));
            item.setActive(i == activeIndex);
            items.add(item);
        }
        return items;
    }

    private List<CallWindow> callWindows(List<Token> tokens, int cursor) {
        List<CallWindow> windows = new ArrayList<>();
        for (int i = tokens.size() - 1; i >= 0; i--) {
            Token token = tokens.get(i);
            if (token.getType() != MySqlLexer.LR_BRACKET) {
                continue;
            }
            int closeIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, i);
            int openOffset = token.getStopIndex() + 1;
            int closeOffset = closeIndex >= 0 ? tokens.get(closeIndex).getStartIndex() : cursor;
            if (closeIndex < 0 && cursor < openOffset) {
                continue;
            }
            int nameEnd = i;
            int nameStart = nameStart(tokens, nameEnd);
            if (nameStart < 0 || isNonRoutineCallName(tokens, nameStart, i)) {
                continue;
            }
            List<String> nameParts = objectNameParts(tokens, nameStart, nameEnd);
            if (nameParts.isEmpty() || nameParts.size() > 3) {
                continue;
            }
            boolean procedure = nameStart > 0 && tokens.get(nameStart - 1).getType() == MySqlLexer.CALL;
            windows.add(new CallWindow(procedure, nameParts, i, closeIndex, openOffset, Math.max(openOffset, closeOffset)));
        }
        return windows;
    }

    private boolean dataTypeContext(MysqlSqlCompletionCandidateContext context) {
        return context.ruleSlot().dataTypeReference();
    }

    private int nameStart(List<Token> tokens, int openParenIndex) {
        int nameStart = openParenIndex - 1;
        if (nameStart < 0 || !MysqlSqlCompletionTokenUtil.isIdentifierToken(tokens.get(nameStart))) {
            return -1;
        }
        while (nameStart > 0) {
            Token current = tokens.get(nameStart);
            Token previous = tokens.get(nameStart - 1);
            if (previous.getType() == MySqlLexer.DOT
                    && previous.getStopIndex() + 1 == current.getStartIndex()
                    && nameStart > 1
                    && MysqlSqlCompletionTokenUtil.isIdentifierToken(tokens.get(nameStart - 2))
                    && tokens.get(nameStart - 2).getStopIndex() + 1 == previous.getStartIndex()) {
                nameStart -= 2;
                continue;
            }
            if (MysqlSqlCompletionTokenUtil.isIdentifierToken(previous)
                    && previous.getStopIndex() + 1 == current.getStartIndex()) {
                nameStart--;
                continue;
            }
            break;
        }
        return nameStart;
    }

    private List<String> objectNameParts(List<Token> tokens, int from, int to) {
        List<String> parts = new ArrayList<>();
        StringBuilder part = new StringBuilder();
        for (int i = Math.max(0, from); i < Math.min(to, tokens.size()); i++) {
            Token token = tokens.get(i);
            if (token.getType() == MySqlLexer.DOT) {
                addNamePart(parts, part);
                part.setLength(0);
                continue;
            }
            if (MysqlSqlCompletionTokenUtil.isIdentifierToken(token)) {
                appendNameToken(parts, part, token.getText());
            }
        }
        addNamePart(parts, part);
        return parts;
    }

    private void appendNameToken(List<String> parts, StringBuilder part, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        String[] tokenParts = value.split("\\.");
        for (int i = 0; i < tokenParts.length; i++) {
            if (i > 0) {
                addNamePart(parts, part);
                part.setLength(0);
            }
            part.append(tokenParts[i]);
        }
    }

    private void addNamePart(List<String> parts, StringBuilder part) {
        String value = MysqlSqlCompletionTokenUtil.stripIdentifierQuotes(part.toString());
        if (StringUtils.isNotBlank(value)) {
            parts.add(value);
        }
    }

    private int activeArgumentIndex(List<Token> tokens, CallWindow callWindow, int cursor) {
        int commaCount = 0;
        int depth = 0;
        int from = callWindow.openParenIndex() + 1;
        int to = callWindow.closeParenIndex() >= 0 ? callWindow.closeParenIndex() : tokens.size();
        for (int i = from; i < to; i++) {
            Token token = tokens.get(i);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            int type = token.getType();
            if (type == MySqlLexer.LR_BRACKET) {
                depth++;
            } else if (type == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
            } else if (type == MySqlLexer.COMMA && depth == 0 && token.getStopIndex() < cursor) {
                commaCount++;
            }
        }
        return commaCount;
    }

    private List<ValueRange> argumentRanges(List<Token> tokens, CallWindow callWindow) {
        int from = callWindow.openParenIndex() + 1;
        int to = callWindow.closeParenIndex() >= 0 ? callWindow.closeParenIndex() : tokens.size();
        List<ValueRange> ranges = new ArrayList<>();
        List<Token> part = new ArrayList<>();
        int depth = 0;
        for (int i = from; i < to; i++) {
            Token token = tokens.get(i);
            int type = token.getType();
            if (type == MySqlLexer.LR_BRACKET) {
                depth++;
                part.add(token);
                continue;
            }
            if (type == MySqlLexer.RR_BRACKET) {
                if (depth > 0) {
                    depth--;
                    part.add(token);
                    continue;
                }
                break;
            }
            if (type == MySqlLexer.COMMA && depth == 0) {
                addValueRange(ranges, part, token.getStartIndex());
                part.clear();
                continue;
            }
            part.add(token);
        }
        addValueRange(ranges, part, callWindow.closeOffset());
        return ranges;
    }

    private void addValueRange(List<ValueRange> ranges, List<Token> tokens, int emptyOffset) {
        List<Token> valueTokens = tokens.stream()
                .filter(MysqlSqlCompletionTokenUtil::isDefaultToken)
                .filter(token -> !MysqlSqlCompletionTokenUtil.isSpaceToken(token))
                .toList();
        if (valueTokens.isEmpty()) {
            ranges.add(new ValueRange(emptyOffset, emptyOffset));
            return;
        }
        Token first = valueTokens.get(0);
        Token last = valueTokens.get(valueTokens.size() - 1);
        ranges.add(new ValueRange(first.getStartIndex(), Math.max(first.getStartIndex(), last.getStopIndex() + 1)));
    }

    private ValueRange valueRangeAt(List<ValueRange> argumentRanges, int index, CallWindow callWindow, int cursor) {
        if (index >= 0 && index < argumentRanges.size()) {
            return argumentRanges.get(index);
        }
        int offset = Math.max(callWindow.openOffset(), Math.min(cursor, callWindow.closeOffset()));
        return new ValueRange(offset, offset);
    }

    private boolean isNonRoutineCallName(List<Token> tokens, int nameStart, int openParenIndex) {
        Token token = tokens.get(nameStart);
        return token != null
                && (token.getType() == MySqlLexer.VALUES
                || token.getType() == MySqlLexer.VALUE
                || MysqlSqlCompletionTokenUtil.isDataTypeNameToken(token)
                || declarationListOpenParen(tokens, nameStart, openParenIndex));
    }

    private boolean declarationListOpenParen(List<Token> tokens, int nameStart, int openParenIndex) {
        int previousIndex = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, nameStart - 1);
        if (previousIndex < 0) {
            return false;
        }
        int previousType = tokens.get(previousIndex).getType();
        if (DECLARATION_NAME_INTRODUCERS.contains(previousType)) {
            return true;
        }
        return previousType == MySqlLexer.ON && createIndexStatementBefore(tokens, openParenIndex);
    }

    private boolean createIndexStatementBefore(List<Token> tokens, int openParenIndex) {
        int statementStart = statementStartIndex(tokens, openParenIndex);
        return MysqlSqlCompletionTokenUtil.hasOrderedTokenTypesBetween(tokens, statementStart, openParenIndex,
                MySqlLexer.CREATE, MySqlLexer.INDEX)
                && statementStart <= openParenIndex;
    }

    private int statementStartIndex(List<Token> tokens, int beforeIndex) {
        for (int index = Math.min(beforeIndex - 1, tokens.size() - 1); index >= 0; index--) {
            if (tokens.get(index).getType() == MySqlLexer.SEMI) {
                return index + 1;
            }
        }
        return 0;
    }

    private String parameterName(SqlCompletionCandidate candidate) {
        if (candidate == null) {
            return "";
        }
        return StringUtils.defaultIfBlank(candidate.getColumnName(), candidate.getLabel());
    }

    private String parameterType(SqlCompletionCandidate candidate) {
        if (candidate == null) {
            return null;
        }
        return StringUtils.defaultIfBlank(candidate.getDataType(), candidate.getDetail());
    }

    private SqlCompletionEditorHint.Range range(String sourceSql, int sourceOffset, int startOffset, int endOffset) {
        return SqlCompletionEditorHint.Range.ofOffsets(sourceSql, sourceOffset + startOffset, sourceOffset + endOffset);
    }

    private record CallWindow(boolean procedure, List<String> nameParts, int openParenIndex,
                              int closeParenIndex, int openOffset, int closeOffset) {
    }

    private record ValueRange(int startOffset, int endOffset) {
    }
}
