package ai.chat2db.plugin.mysql.completion.analysis.statement.dml.insert;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionInsertColumnListContext;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionInsertStatementContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;


public final class MysqlInsertStatementAnalyzer {

    public MysqlSqlCompletionInsertStatementContext analyze(SqlCompletionStatementWindow window,
                                                            SqlCompletionCursorContext cursorContext) {
        if (!canAnalyze(window, cursorContext)) {
            return MysqlSqlCompletionInsertStatementContext.inactive();
        }
        String sql = Objects.toString(window.parseSql(), "");
        List<Token> tokens = MysqlSqlCompletionTokenUtil.significantDefaultTokens(sql);
        int cursor = cursorOffset(window, cursorContext);
        int semanticCursor = semanticCursorOffset(window, cursorContext);

        for (int insertIndex = lastInsertLikeIndexBefore(tokens, semanticCursor); insertIndex >= 0;
                insertIndex = previousInsertLikeIndexBefore(tokens, insertIndex)) {
            MysqlSqlCompletionInsertStatementContext context =
                    analyzeInsertLikeStatement(tokens, cursor, semanticCursor, cursorContext.dotScoped(), insertIndex);
            if (context.active()) {
                return context;
            }
        }
        return MysqlSqlCompletionInsertStatementContext.inactive();
    }

    private MysqlSqlCompletionInsertStatementContext analyzeInsertLikeStatement(List<Token> tokens,
                                                                               int cursor,
                                                                               int semanticCursor,
                                                                               boolean dotScoped,
                                                                               int insertIndex) {
        int tableStartIndex = targetTableStartIndex(tokens, semanticCursor, insertIndex);
        if (tableStartIndex < 0 || tableStartIndex >= tokens.size()) {
            return MysqlSqlCompletionInsertStatementContext.inactive();
        }

        int tableEndIndex = MysqlSqlCompletionTokenUtil.qualifiedIdentifierEndIndex(tokens, tableStartIndex);
        if (tableEndIndex < tableStartIndex) {
            return MysqlSqlCompletionInsertStatementContext.inactive();
        }

        MysqlSqlCompletionInsertStatementContext.TableRef tableRef = tableRef(tokens, tableStartIndex, tableEndIndex);
        if (tableRef == null || StringUtils.isBlank(tableRef.table())) {
            return MysqlSqlCompletionInsertStatementContext.inactive();
        }

        int listStartIndex = targetColumnListStartIndex(tokens, tableEndIndex);
        MysqlSqlCompletionInsertStatementContext.ColumnWindow columnWindow = columnWindow(tokens, listStartIndex);
        MysqlSqlCompletionInsertColumnListContext columnListContext =
                columnListContext(tokens, semanticCursor, dotScoped, listStartIndex, tableRef);
        int valuesIndex = firstIndexOfAnyAfter(tokens, columnWindow.closeParenIndex() + 1,
                MySqlLexer.VALUES, MySqlLexer.VALUE);
        List<MysqlSqlCompletionInsertStatementContext.RowWindow> valueRows =
                valuesIndex < 0 ? List.of() : valueRows(tokens, valuesIndex + 1, cursor, columnWindow.columns().size());

        if (!columnListContext.active() && columnWindow.columns().isEmpty() && valueRows.isEmpty()) {
            return MysqlSqlCompletionInsertStatementContext.inactive();
        }
        return MysqlSqlCompletionInsertStatementContext.active(tableRef, columnWindow, valueRows, columnListContext);
    }

    private boolean canAnalyze(SqlCompletionStatementWindow window, SqlCompletionCursorContext cursorContext) {
        return window != null && cursorContext != null && cursorContext.admitted();
    }

    private int cursorOffset(SqlCompletionStatementWindow window, SqlCompletionCursorContext cursorContext) {
        return Math.max(0, Math.min(window.cursor(), StringUtils.length(window.parseSql())));
    }

    private int semanticCursorOffset(SqlCompletionStatementWindow window, SqlCompletionCursorContext cursorContext) {
        return Math.max(0, Math.min(cursorContext.replaceStart(), StringUtils.length(window.parseSql())));
    }

    private int targetTableStartIndex(List<Token> tokens, int cursor, int insertIndex) {
        int intoIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBefore(tokens, insertIndex + 1, cursor,
                MySqlLexer.INTO);
        if (intoIndex >= 0) {
            return MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, intoIndex + 1);
        }
        if (tokens.get(insertIndex).getType() == MySqlLexer.REPLACE) {
            return MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, insertIndex + 1);
        }
        return -1;
    }

    private MysqlSqlCompletionInsertStatementContext.TableRef tableRef(List<Token> tokens,
                                                                       int tableStartIndex,
                                                                       int tableEndIndex) {
        List<String> parts = objectNameParts(tokens, tableStartIndex, tableEndIndex + 1);
        if (parts.isEmpty()) {
            return null;
        }
        if (parts.size() == 1) {
            return new MysqlSqlCompletionInsertStatementContext.TableRef(null, null, parts.get(0));
        }
        if (parts.size() == 2) {
            return new MysqlSqlCompletionInsertStatementContext.TableRef(null, parts.get(0), parts.get(1));
        }
        return new MysqlSqlCompletionInsertStatementContext.TableRef(parts.get(parts.size() - 3),
                parts.get(parts.size() - 2), parts.get(parts.size() - 1));
    }

    private int targetColumnListStartIndex(List<Token> tokens, int tableEndIndex) {
        return MysqlSqlCompletionTokenUtil.nextDefaultIndexIfType(tokens, tableEndIndex + 1, MySqlLexer.LR_BRACKET);
    }

    private MysqlSqlCompletionInsertStatementContext.ColumnWindow columnWindow(List<Token> tokens, int listStartIndex) {
        if (listStartIndex < 0) {
            return MysqlSqlCompletionInsertStatementContext.ColumnWindow.empty();
        }
        int listEndIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, listStartIndex);
        if (listEndIndex < 0) {
            return MysqlSqlCompletionInsertStatementContext.ColumnWindow.empty();
        }
        List<MysqlSqlCompletionInsertStatementContext.ColumnRange> columns =
                splitColumnRanges(tokens, listStartIndex + 1, listEndIndex);
        return new MysqlSqlCompletionInsertStatementContext.ColumnWindow(listStartIndex, listEndIndex, columns);
    }

    private MysqlSqlCompletionInsertColumnListContext columnListContext(
            List<Token> tokens,
            int cursor,
            boolean dotScoped,
            int listStartIndex,
            MysqlSqlCompletionInsertStatementContext.TableRef tableRef) {
        if (dotScoped || tableRef == null || !cursorInsideTargetColumnList(tokens, cursor, listStartIndex)) {
            return MysqlSqlCompletionInsertColumnListContext.inactive();
        }
        List<String> writtenColumns = writtenColumnNames(tokens, listStartIndex + 1, cursor);
        return MysqlSqlCompletionInsertColumnListContext.active(tableRef.table(), writtenColumns);
    }

    private boolean cursorInsideTargetColumnList(List<Token> tokens, int cursor, int listStartIndex) {
        return MysqlSqlCompletionTokenUtil.cursorInsideOpenEndedBracketRange(tokens, listStartIndex, cursor);
    }

    private List<MysqlSqlCompletionInsertStatementContext.ColumnRange> splitColumnRanges(List<Token> tokens,
                                                                                        int from,
                                                                                        int to) {
        List<MysqlSqlCompletionInsertStatementContext.ColumnRange> columns = new ArrayList<>();
        List<Token> part = new ArrayList<>();
        int depth = 0;
        for (int i = Math.max(0, from); i < Math.min(to, tokens.size()); i++) {
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
                }
                part.add(token);
                continue;
            }
            if (type == MySqlLexer.COMMA && depth == 0) {
                addColumnRange(columns, part);
                part.clear();
                continue;
            }
            part.add(token);
        }
        addColumnRange(columns, part);
        return columns;
    }

    private void addColumnRange(List<MysqlSqlCompletionInsertStatementContext.ColumnRange> columns,
                                List<Token> tokens) {
        List<Token> nameTokens = tokens.stream()
                .filter(MysqlSqlCompletionTokenUtil::isIdentifierToken)
                .toList();
        if (nameTokens.isEmpty()) {
            return;
        }
        Token first = nameTokens.get(0);
        Token last = nameTokens.get(nameTokens.size() - 1);
        String name = nameTokens.stream()
                .map(Token::getText)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(""));
        String normalizedName = MysqlSqlCompletionTokenUtil.stripIdentifierQuotes(name);
        if (StringUtils.isBlank(normalizedName)) {
            return;
        }
        columns.add(new MysqlSqlCompletionInsertStatementContext.ColumnRange(normalizedName,
                first.getStartIndex(), last.getStopIndex() + 1));
    }

    private List<String> writtenColumnNames(List<Token> tokens, int startIndex, int cursor) {
        List<String> columns = new ArrayList<>();
        int depth = 0;
        for (int index = Math.max(0, startIndex); index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!MysqlSqlCompletionTokenUtil.isDefaultToken(token) || token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET && depth == 0) {
                break;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
                continue;
            }
            if (depth != 0 || !MysqlSqlCompletionTokenUtil.isIdentifierToken(token)) {
                continue;
            }
            String column = columnIdentifier(tokens, index);
            if (StringUtils.isNotBlank(column)) {
                columns.add(column);
            }
        }
        return columns;
    }

    private String columnIdentifier(List<Token> tokens, int index) {
        Token token = tokens.get(index);
        if (token.getType() == MySqlLexer.DOT_ID) {
            return MysqlSqlCompletionTokenUtil.stripLeadingDot(token.getText());
        }
        int nextIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, index + 1);
        if (nextIndex >= 0 && (tokens.get(nextIndex).getType() == MySqlLexer.DOT
                || tokens.get(nextIndex).getType() == MySqlLexer.DOT_ID)) {
            return "";
        }
        return MysqlSqlCompletionTokenUtil.identifierText(token);
    }

    private List<MysqlSqlCompletionInsertStatementContext.RowWindow> valueRows(List<Token> tokens,
                                                                               int from,
                                                                               int cursor,
                                                                               int columnCount) {
        List<MysqlSqlCompletionInsertStatementContext.RowWindow> rows = new ArrayList<>();
        int rowIndex = 0;
        for (int i = Math.max(0, from); i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() != MySqlLexer.LR_BRACKET) {
                if (isValuesClauseStop(token)) {
                    return rows;
                }
                continue;
            }
            int closeIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, i);
            int rowStart = token.getStopIndex() + 1;
            int rowEnd = closeIndex >= 0 ? tokens.get(closeIndex).getStartIndex() : cursor;
            int safeRowEnd = Math.max(rowStart, rowEnd);
            boolean active = cursor >= rowStart && cursor <= safeRowEnd;
            int activeColumnIndex = active ? activeColumnIndex(tokens, i, closeIndex, columnCount, cursor) : -1;
            List<MysqlSqlCompletionInsertStatementContext.ValueRange> valueRanges = valueRanges(tokens, i, closeIndex,
                    safeRowEnd);
            rows.add(new MysqlSqlCompletionInsertStatementContext.RowWindow(rowIndex, i, closeIndex, rowStart,
                    safeRowEnd, active, activeColumnIndex, valueRanges));
            rowIndex++;
            if (closeIndex >= 0) {
                i = closeIndex;
            }
        }
        return rows;
    }

    private List<MysqlSqlCompletionInsertStatementContext.ValueRange> valueRanges(List<Token> tokens,
                                                                                  int rowOpenParenIndex,
                                                                                  int rowCloseParenIndex,
                                                                                  int rowEndOffset) {
        int from = rowOpenParenIndex + 1;
        int to = rowCloseParenIndex >= 0 ? rowCloseParenIndex : tokens.size();
        List<MysqlSqlCompletionInsertStatementContext.ValueRange> ranges = new ArrayList<>();
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
        addValueRange(ranges, part, rowEndOffset);
        return ranges;
    }

    private void addValueRange(List<MysqlSqlCompletionInsertStatementContext.ValueRange> ranges,
                               List<Token> tokens,
                               int emptyOffset) {
        List<Token> valueTokens = tokens.stream()
                .filter(MysqlSqlCompletionTokenUtil::isDefaultToken)
                .filter(token -> !MysqlSqlCompletionTokenUtil.isSpaceToken(token))
                .toList();
        if (valueTokens.isEmpty()) {
            ranges.add(new MysqlSqlCompletionInsertStatementContext.ValueRange(emptyOffset, emptyOffset));
            return;
        }
        Token first = valueTokens.get(0);
        Token last = valueTokens.get(valueTokens.size() - 1);
        ranges.add(new MysqlSqlCompletionInsertStatementContext.ValueRange(first.getStartIndex(),
                Math.max(first.getStartIndex(), last.getStopIndex() + 1)));
    }

    private int activeColumnIndex(List<Token> tokens,
                                  int rowOpenParenIndex,
                                  int rowCloseParenIndex,
                                  int columnCount,
                                  int cursor) {
        if (columnCount <= 0) {
            return -1;
        }
        int from = rowOpenParenIndex + 1;
        int to = rowCloseParenIndex >= 0 ? rowCloseParenIndex : tokens.size();
        int commaCount = 0;
        int depth = 0;
        for (int i = from; i < to; i++) {
            Token token = tokens.get(i);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            int type = token.getType();
            if (type == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (type == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
                continue;
            }
            if (type == MySqlLexer.COMMA && depth == 0 && token.getStopIndex() < cursor) {
                commaCount++;
            }
        }
        return Math.min(commaCount, columnCount - 1);
    }

    private int lastInsertLikeIndexBefore(List<Token> tokens, int cursor) {
        return MysqlSqlCompletionTokenUtil.lastDefaultIndexBeforeAtDepth(tokens, cursor, 0,
                MySqlLexer.INSERT, MySqlLexer.REPLACE);
    }

    private int previousInsertLikeIndexBefore(List<Token> tokens, int beforeIndex) {
        return MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, beforeIndex - 1, MySqlLexer.INSERT,
                MySqlLexer.REPLACE);
    }

    private int firstIndexOfAnyAfter(List<Token> tokens, int start, int... tokenTypes) {
        for (int i = Math.max(0, start); i < tokens.size(); i++) {
            int type = tokens.get(i).getType();
            for (int tokenType : tokenTypes) {
                if (type == tokenType) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean isValuesClauseStop(Token token) {
        return token != null && token.getType() == MySqlLexer.SEMI;
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
}
