package ai.chat2db.plugin.mysql.model.completion.context;

import java.util.List;
import org.apache.commons.lang3.StringUtils;


public record MysqlSqlCompletionInsertStatementContext(boolean active,
                                                       TableRef tableRef,
                                                       ColumnWindow columnWindow,
                                                       List<RowWindow> valueRows,
                                                       MysqlSqlCompletionInsertColumnListContext columnListContext) {

    private static final MysqlSqlCompletionInsertStatementContext INACTIVE =
            new MysqlSqlCompletionInsertStatementContext(false, null, ColumnWindow.empty(), List.of(),
                    MysqlSqlCompletionInsertColumnListContext.inactive());

    public MysqlSqlCompletionInsertStatementContext {
        columnWindow = columnWindow == null ? ColumnWindow.empty() : columnWindow;
        valueRows = valueRows == null ? List.of() : List.copyOf(valueRows);
        columnListContext = columnListContext == null
                ? MysqlSqlCompletionInsertColumnListContext.inactive()
                : columnListContext;
    }

    public static MysqlSqlCompletionInsertStatementContext inactive() {
        return INACTIVE;
    }

    public static MysqlSqlCompletionInsertStatementContext active(TableRef tableRef,
                                                                  ColumnWindow columnWindow,
                                                                  List<RowWindow> valueRows,
                                                                  MysqlSqlCompletionInsertColumnListContext columnListContext) {
        if (tableRef == null || StringUtils.isBlank(tableRef.table())) {
            return inactive();
        }
        return new MysqlSqlCompletionInsertStatementContext(true, tableRef, columnWindow, valueRows, columnListContext);
    }

    public boolean hasExplicitColumnList() {
        return !columnWindow.columns().isEmpty();
    }

    public boolean hasValueRows() {
        return !valueRows.isEmpty();
    }

    public record TableRef(String catalog, String schema, String table) {
    }

    public record ColumnWindow(int openParenIndex,
                               int closeParenIndex,
                               List<ColumnRange> columns) {

        private static final ColumnWindow EMPTY = new ColumnWindow(-1, -1, List.of());

        public ColumnWindow {
            columns = columns == null ? List.of() : List.copyOf(columns);
        }

        public static ColumnWindow empty() {
            return EMPTY;
        }
    }

    public record ColumnRange(String name, int startOffset, int endOffset) {
    }

    public record RowWindow(int rowIndex,
                            int openParenIndex,
                            int closeParenIndex,
                            int rowStartOffset,
                            int rowEndOffset,
                            boolean active,
                            int activeColumnIndex,
                            List<ValueRange> valueRanges) {

        public RowWindow {
            valueRanges = valueRanges == null ? List.of() : List.copyOf(valueRanges);
        }
    }

    public record ValueRange(int startOffset, int endOffset) {
    }
}
