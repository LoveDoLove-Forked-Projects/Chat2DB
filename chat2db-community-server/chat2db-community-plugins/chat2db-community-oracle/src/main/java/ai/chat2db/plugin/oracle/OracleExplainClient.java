package ai.chat2db.plugin.oracle;

import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.result.ExecutionContext;
import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.ResultCell;
import ai.chat2db.spi.model.ExecutionTiming;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Runs Oracle {@code EXPLAIN PLAN} and reads the resulting plan.
 *
 * <p>{@code EXPLAIN PLAN ... FOR <statement>} writes plan rows into the plan
 * table and returns no result set, so the plan is read back with a separate
 * query: formatted text through {@code DBMS_XPLAN}, and the plan table rows
 * themselves for callers that need the per step columns.
 */
final class OracleExplainClient {

    private static final String PLAN_TABLE = "PLAN_TABLE";
    private static final String PLAN_TABLE_PROBE_SQL = "SELECT 1 FROM " + PLAN_TABLE + " WHERE ROWNUM = 1";
    private static final String EXPLAIN_PLAN_SQL =
            "EXPLAIN PLAN SET STATEMENT_ID = '%s' INTO " + PLAN_TABLE + " FOR ";
    private static final String DISPLAY_PLAN_SQL =
            "SELECT PLAN_TABLE_OUTPUT FROM TABLE(DBMS_XPLAN.DISPLAY('" + PLAN_TABLE + "', ?, 'TYPICAL'))";
    private static final String PLAN_ROWS_SQL = "SELECT ID, PARENT_ID, DEPTH, OPERATION, OPTIONS, OBJECT_NAME,"
            + " OBJECT_TYPE, COST, CARDINALITY, BYTES, ACCESS_PREDICATES, FILTER_PREDICATES FROM " + PLAN_TABLE
            + " WHERE STATEMENT_ID = ? ORDER BY ID";
    private static final String STATEMENT_ID_PREFIX = "CHAT2DB_";
    private static final int STATEMENT_ID_MAX_LENGTH = 30;

    List<ExecuteResponse> explain(Connection connection, String explainedSql, ExecutionContext executionContext)
            throws SQLException {
        requirePlanTable(connection);
        String statementId = newStatementId();
        // Plan rows only stay readable while the write is part of one transaction,
        // so auto commit connections are held inside a transaction until the plan
        // has been read. Caller managed transactions are left untouched.
        boolean transactionManaged = connection.getAutoCommit();
        if (transactionManaged) {
            connection.setAutoCommit(false);
        }
        try {
            long startedAtEpochMs = System.currentTimeMillis();
            long executeStartedNanos = System.nanoTime();
            runExplainPlan(connection, statementId, explainedSql);
            long executeDurationNanos = ExecutionTiming.elapsedNanos(executeStartedNanos);

            ResultTable planRows = queryRows(connection, PLAN_ROWS_SQL, statementId);
            if (planRows.isEmpty()) {
                throw new SQLException("Oracle EXPLAIN PLAN wrote no plan rows into " + PLAN_TABLE
                        + " for statement id " + statementId);
            }
            List<ExecuteResponse> responses = new ArrayList<>();
            ResultTable planText = queryRowsQuietly(connection, DISPLAY_PLAN_SQL, statementId);
            if (planText != null && !planText.isEmpty()) {
                responses.add(buildResponse(planText, executionContext, startedAtEpochMs, executeDurationNanos,
                        responses.size() + 1));
            }
            responses.add(buildResponse(planRows, executionContext, startedAtEpochMs, executeDurationNanos,
                    responses.size() + 1));
            return responses;
        } finally {
            restoreTransaction(connection, transactionManaged);
        }
    }

    private static void requirePlanTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(PLAN_TABLE_PROBE_SQL)) {
            // The probe only proves that the plan table can be read and written.
        } catch (SQLException e) {
            throw new SQLException("Oracle plan table " + PLAN_TABLE + " is not available for the current user."
                    + " Create it with @?/rdbms/admin/utlxplan.sql and run EXPLAIN PLAN again.", e);
        }
    }

    private static void runExplainPlan(Connection connection, String statementId, String explainedSql)
            throws SQLException {
        String explainSql = String.format(EXPLAIN_PLAN_SQL, statementId) + explainedSql;
        try (Statement statement = connection.createStatement()) {
            statement.execute(explainSql);
        }
    }

    private static String newStatementId() {
        String uniqueId = UUID.randomUUID().toString().replace("-", "");
        return STATEMENT_ID_PREFIX + uniqueId.substring(0, STATEMENT_ID_MAX_LENGTH - STATEMENT_ID_PREFIX.length());
    }

    private static ResultTable queryRows(Connection connection, String sql, String statementId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statementId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return readTable(resultSet);
            }
        }
    }

    /**
     * Reads an optional plan representation. {@code DBMS_XPLAN} is not granted
     * to every user, and the plan table rows are the authoritative result, so a
     * rejected display query must not fail the whole explain.
     */
    private static ResultTable queryRowsQuietly(Connection connection, String sql, String statementId) {
        try {
            return queryRows(connection, sql, statementId);
        } catch (SQLException e) {
            return null;
        }
    }

    private static ResultTable readTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Header> headerList = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            headerList.add(Header.builder()
                    .name(metaData.getColumnLabel(i))
                    .dataType(metaData.getColumnTypeName(i))
                    .build());
        }
        List<List<ResultCell>> dataList = new ArrayList<>();
        while (resultSet.next()) {
            List<ResultCell> row = new ArrayList<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                row.add(ResultCell.of(resultSet.getString(i)));
            }
            dataList.add(row);
        }
        return new ResultTable(headerList, dataList);
    }

    private static ExecuteResponse buildResponse(ResultTable table, ExecutionContext executionContext,
                                                 long startedAtEpochMs, long executeDurationNanos, int resultSetId) {
        return ExecuteResponse.builder()
                .success(Boolean.TRUE)
                .sqlType(SqlTypeEnum.EXPLAIN.name())
                .headerList(table.headerList())
                .dataList(table.dataList())
                .resultSetId(resultSetId)
                .hasNextPage(Boolean.FALSE)
                .executionContext(executionContext)
                .executionMetrics(ExecutionTiming.complete(ExecutionTiming.started(startedAtEpochMs),
                        executeDurationNanos, 0L, table.dataList().size()))
                .build();
    }

    private static void restoreTransaction(Connection connection, boolean transactionManaged) {
        if (!transactionManaged) {
            return;
        }
        try {
            // The plan has been read already; rolling back only drops the plan rows
            // written by this call.
            connection.rollback();
        } catch (SQLException ignored) {
            // Restoring auto commit below is what keeps the connection reusable.
        }
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
            // The caller owns the connection and reports its own failures.
        }
    }

    private record ResultTable(List<Header> headerList, List<List<ResultCell>> dataList) {

        boolean isEmpty() {
            return dataList.isEmpty();
        }
    }
}
