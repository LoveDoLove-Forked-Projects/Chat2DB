package ai.chat2db.plugin.oracle;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.sql.SimpleSqlStatement;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OracleCommandExecutorTest {

    private static final String PROBE_SQL = "SELECT 1 FROM PLAN_TABLE WHERE ROWNUM = 1";
    private static final String DISPLAY_SQL_PREFIX = "SELECT PLAN_TABLE_OUTPUT FROM TABLE(DBMS_XPLAN.DISPLAY(";
    private static final String PLAN_ROWS_SQL_PREFIX = "SELECT ID, PARENT_ID, DEPTH, OPERATION";

    @Test
    void allOracleSqlShouldUseOracleExecutor() {
        assertSame(OracleCommandExecutor.INSTANCE, new OracleMetaData().getCommandExecutor());
    }

    @Test
    void explainShouldWriteThenReadThePlanBackAsTextAndRows() throws Exception {
        FakeOracle oracle = new FakeOracle();

        List<ExecuteResponse> results = executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle);

        assertEquals(2, results.size());
        assertEquals("PLAN_TABLE_OUTPUT", results.get(0).getHeaderList().get(0).getName());
        assertEquals("| Id | Operation |", results.get(0).getDataList().get(0).get(0).getValue());
        assertEquals("OPERATION", results.get(1).getHeaderList().get(3).getName());
        assertEquals("TABLE ACCESS FULL", results.get(1).getDataList().get(0).get(3).getValue());
        assertEquals(1, results.get(0).getResultSetId());
        assertEquals(2, results.get(1).getResultSetId());
        assertTrue(results.get(0).getSuccess());
    }

    @Test
    void explainShouldProbeThePlanTableAndExplainTheInnerStatement() throws Exception {
        FakeOracle oracle = new FakeOracle();

        executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle);

        assertTrue(oracle.executedSql.contains(PROBE_SQL));
        String explainSql = oracle.explainSql();
        assertTrue(explainSql.startsWith("EXPLAIN PLAN SET STATEMENT_ID = 'CHAT2DB_"), explainSql);
        assertTrue(explainSql.endsWith("' INTO PLAN_TABLE FOR select 1 from dual"), explainSql);
        assertTrue(oracle.preparedSql.stream().anyMatch(sql -> sql.startsWith(PLAN_ROWS_SQL_PREFIX)));
        assertTrue(oracle.preparedSql.stream().anyMatch(sql -> sql.startsWith(DISPLAY_SQL_PREFIX)));
    }

    @Test
    void explainShouldUseOneStatementIdWithinThePlanTableColumnLimit() throws Exception {
        FakeOracle oracle = new FakeOracle();

        executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle);

        assertEquals(1, oracle.uniqueStatementIds().size());
        String statementId = oracle.uniqueStatementIds().get(0);
        assertTrue(statementId.length() <= 30, statementId);
        assertTrue(oracle.explainSql().contains("'" + statementId + "'"));
    }

    @Test
    void explainShouldGenerateANewStatementIdForEveryCall() throws Exception {
        FakeOracle oracle = new FakeOracle();

        executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle);
        executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle);

        assertEquals(2, oracle.uniqueStatementIds().size());
        assertFalse(oracle.uniqueStatementIds().get(0).equals(oracle.uniqueStatementIds().get(1)));
    }

    @Test
    void explainShouldKeepTheWrittenPlanInsideOneTransaction() throws Exception {
        FakeOracle oracle = new FakeOracle();

        executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle);

        assertEquals(2, oracle.setAutoCommitCount);
        assertTrue(oracle.autoCommit);
        assertEquals(1, oracle.rollbackCount);
    }

    @Test
    void explainShouldNotTouchACallerManagedTransaction() throws Exception {
        FakeOracle oracle = new FakeOracle();
        oracle.autoCommit = false;

        executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle);

        assertEquals(0, oracle.setAutoCommitCount);
        assertEquals(0, oracle.rollbackCount);
        assertFalse(oracle.autoCommit);
    }

    @Test
    void explainShouldStillReturnPlanRowsWhenDisplayPlanIsRejected() throws Exception {
        FakeOracle oracle = new FakeOracle();
        oracle.displayPlanRejected = true;

        List<ExecuteResponse> results = executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle);

        assertEquals(1, results.size());
        assertEquals("OPERATION", results.get(0).getHeaderList().get(3).getName());
        assertEquals(1, results.get(0).getResultSetId());
    }

    @Test
    void explainShouldReportAMissingPlanTableWithoutWritingAnything() {
        FakeOracle oracle = new FakeOracle();
        oracle.planTableMissing = true;

        SQLException failure = assertThrows(SQLException.class,
                () -> executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle));

        assertTrue(failure.getMessage().contains("PLAN_TABLE"));
        assertFalse(oracle.hasExplainPlan());
    }

    @Test
    void explainShouldReportAPlanThatWasNeverWritten() {
        FakeOracle oracle = new FakeOracle();
        oracle.planRowsMissing = true;

        SQLException failure = assertThrows(SQLException.class,
                () -> executeMulti("EXPLAIN PLAN FOR select 1 from dual", oracle));

        assertTrue(failure.getMessage().contains("no plan rows"));
    }

    private static List<ExecuteResponse> executeMulti(String sql, FakeOracle oracle) throws SQLException {
        return OracleCommandExecutor.INSTANCE.executeMulti(new SimpleSqlStatement(sql), oracle.connection(), false,
                null, null, null, null);
    }

    /**
     * Minimal JDBC stand in for the statements an Oracle explain issues: the
     * plan table probe, the explain itself, and the two plan queries. Any other
     * SQL falls back to an empty update so the default execution path stays
     * reachable.
     */
    private static final class FakeOracle implements InvocationHandler {

        private final List<String> executedSql = new ArrayList<>();
        private final List<String> preparedSql = new ArrayList<>();
        private final List<String> statementIds = new ArrayList<>();
        private boolean autoCommit = true;
        private int setAutoCommitCount;
        private int rollbackCount;
        private boolean planTableMissing;
        private boolean displayPlanRejected;
        private boolean planRowsMissing;

        Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Connection.class},
                    this);
        }

        String explainSql() {
            return executedSql.stream()
                    .filter(sql -> sql.startsWith("EXPLAIN PLAN"))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("no EXPLAIN PLAN was executed: " + executedSql));
        }

        boolean hasExplainPlan() {
            return executedSql.stream().anyMatch(sql -> sql.startsWith("EXPLAIN PLAN"));
        }

        List<String> uniqueStatementIds() {
            return statementIds.stream().distinct().toList();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "createStatement":
                    return statement();
                case "prepareStatement":
                    return preparedStatement((String) args[0]);
                case "getAutoCommit":
                    return autoCommit;
                case "setAutoCommit":
                    autoCommit = (Boolean) args[0];
                    setAutoCommitCount++;
                    return null;
                case "rollback":
                    rollbackCount++;
                    return null;
                case "clearWarnings":
                case "close":
                    return null;
                default:
                    throw new UnsupportedOperationException("unexpected connection call: " + method.getName());
            }
        }

        private Object statement() {
            return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{java.sql.Statement.class},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "executeQuery":
                                executedSql.add((String) args[0]);
                                if (planTableMissing) {
                                    throw new SQLException("ORA-00942: table or view does not exist");
                                }
                                return rows(List.of("1"), List.of());
                            case "execute":
                                executedSql.add((String) args[0]);
                                return false;
                            case "close":
                                return null;
                            default:
                                throw new UnsupportedOperationException("unexpected statement call: "
                                        + method.getName());
                        }
                    });
        }

        private Object preparedStatement(String sql) {
            preparedSql.add(sql);
            return Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[]{java.sql.PreparedStatement.class}, (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "setString":
                                statementIds.add((String) args[1]);
                                return null;
                            case "executeQuery":
                                return planRows(sql);
                            case "execute":
                                return false;
                            case "getUpdateCount":
                                return -1;
                            case "getMoreResults":
                                return false;
                            case "getWarnings":
                                return null;
                            case "setFetchSize":
                            case "close":
                                return null;
                            default:
                                throw new UnsupportedOperationException("unexpected prepared call: "
                                        + method.getName());
                        }
                    });
        }

        private ResultSet planRows(String sql) throws SQLException {
            if (sql.startsWith(DISPLAY_SQL_PREFIX)) {
                if (displayPlanRejected) {
                    throw new SQLException("ORA-00904: DBMS_XPLAN: invalid identifier");
                }
                return rows(List.of("PLAN_TABLE_OUTPUT"), List.of(List.of("| Id | Operation |")));
            }
            if (sql.startsWith(PLAN_ROWS_SQL_PREFIX)) {
                if (planRowsMissing) {
                    return rows(List.of("ID", "PARENT_ID", "DEPTH", "OPERATION", "OPTIONS", "OBJECT_NAME",
                            "OBJECT_TYPE", "COST", "CARDINALITY", "BYTES", "ACCESS_PREDICATES", "FILTER_PREDICATES"),
                            List.of());
                }
                return rows(
                        List.of("ID", "PARENT_ID", "DEPTH", "OPERATION", "OPTIONS", "OBJECT_NAME", "OBJECT_TYPE",
                                "COST", "CARDINALITY", "BYTES", "ACCESS_PREDICATES", "FILTER_PREDICATES"),
                        List.of(Arrays.asList("0", null, "0", "TABLE ACCESS FULL", "FULL", "DUAL", "TABLE", "2", "1",
                                "2", null, null)));
            }
            throw new UnsupportedOperationException("unexpected query: " + sql);
        }

        private ResultSet rows(List<String> columns, List<List<String>> data) {
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ResultSet.class},
                    new Rows(columns, data));
        }

        private Object resultSetMetaData(List<String> columns) {
            return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ResultSetMetaData.class},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "getColumnCount":
                                return columns.size();
                            case "getColumnLabel":
                            case "getColumnTypeName":
                                return columns.get((Integer) args[0] - 1).toUpperCase(Locale.ROOT);
                            default:
                                throw new UnsupportedOperationException("unexpected metadata call: "
                                        + method.getName());
                        }
                    });
        }

        private final class Rows implements InvocationHandler {

            private final List<String> columns;
            private final List<List<String>> data;
            private int index = -1;

            private Rows(List<String> columns, List<List<String>> data) {
                this.columns = columns;
                this.data = data;
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                switch (method.getName()) {
                    case "next":
                        return ++index < data.size();
                    case "getString":
                        return data.get(index).get((Integer) args[0] - 1);
                    case "getMetaData":
                        return resultSetMetaData(columns);
                    case "close":
                        return null;
                    default:
                        throw new UnsupportedOperationException("unexpected result set call: " + method.getName());
                }
            }
        }
    }
}
