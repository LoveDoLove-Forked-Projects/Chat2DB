package ai.chat2db.community.start.test.generate;

import ai.chat2db.spi.util.SqlGenerateUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlCountGeneratorTest {

    @Test
    void mysqlTestWithClause() throws JSQLParserException {
        String originalSql = """
                WITH approval_ids AS (
                    SELECT id
                    FROM access_control_approval_process
                )
                SELECT *
                FROM access_control_apply_record a
                WHERE a.id IN (SELECT id FROM approval_ids)
                """;

        String expectedSql = """
                WITH approval_ids AS (SELECT id FROM access_control_approval_process)
                SELECT COUNT(*) FROM access_control_apply_record a WHERE a.id IN (SELECT id FROM approval_ids)
                """.replaceAll("\\s+", " ").trim();

        String actualSql = SqlGenerateUtil.generateSelectCountSql(originalSql, DbType.mysql.name())
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expectedSql, actualSql);
    }

    @Test
    void mysqlTestJoinQuery() throws JSQLParserException {
        String originalSql = """
                SELECT *
                FROM access_control_apply_record a
                JOIN access_control_approval_process acap ON a.id = acap.id
                LIMIT 10
                """;

        String expectedSql = """
                SELECT COUNT(*) FROM access_control_apply_record a
                JOIN access_control_approval_process acap ON a.id = acap.id
                """.replaceAll("\\s+", " ").trim();

        String actualSql = SqlGenerateUtil.generateSelectCountSql(originalSql, DbType.mysql.name())
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expectedSql, actualSql);
    }

    @Test
    void mysqlTestInSubQuery() throws JSQLParserException {
        String originalSql = """
                SELECT *
                FROM access_control_apply_record a
                WHERE a.id IN (
                    SELECT id
                    FROM access_control_approval_process
                )
                ORDER BY a.create_time
                """;

        String expectedSql = """
                SELECT COUNT(*) FROM access_control_apply_record a
                WHERE a.id IN (SELECT id FROM access_control_approval_process)
                """.replaceAll("\\s+", " ").trim();

        String actualSql = SqlGenerateUtil.generateSelectCountSql(originalSql, DbType.mysql.name())
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expectedSql, actualSql);
    }

    @Test
    void sqlServerTopJoinQueryTest() throws JSQLParserException {
        String originalSql = """
                    SELECT TOP 10 *
                    FROM access_control_apply_record a
                    INNER JOIN access_control_approval_process acap\s
                        ON a.id = acap.id
                    ORDER BY a.apply_time DESC;
                """;

        String expectedSql = """
                    SELECT TOP 10 COUNT(*)
                    FROM access_control_apply_record a
                    INNER JOIN access_control_approval_process acap\s
                        ON a.id = acap.id
                """.replaceAll("\\s+", " ").trim();

        String actualSql = SqlGenerateUtil.generateSelectCountSql(originalSql, DbType.mysql.name())
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expectedSql, actualSql);
    }

    @Test
    void sqlServerLimitQueryTest() throws JSQLParserException {
        String originalSql = """
                    SELECT *
                        FROM access_control_apply_record
                        ORDER BY apply_time DESC
                        OFFSET 0 ROWS
                        FETCH NEXT 10 ROWS ONLY;
                """;

        String expectedSql = """
                  SELECT COUNT(*)
                      FROM access_control_apply_record
                """.replaceAll("\\s+", " ").trim();

        String actualSql = SqlGenerateUtil.generateSelectCountSql(originalSql, DbType.mysql.name())
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expectedSql, actualSql);
    }

    @Test
    void sqlServerWithQueryTest() throws JSQLParserException {
        String originalSql = """
                  WITH RankedApps AS (
                          SELECT
                              id,
                              applicant,
                              apply_time,
                              ROW_NUMBER() OVER (ORDER BY apply_time DESC) AS rn
                          FROM access_control_apply_record
                      )
                      SELECT *
                      FROM RankedApps
                      WHERE rn BETWEEN 1 AND 10;
                """;

        String expectedSql = """
                WITH RankedApps AS (SELECT
                              id,
                              applicant,
                              apply_time,
                              ROW_NUMBER() OVER (ORDER BY apply_time DESC) AS rn
                          FROM access_control_apply_record)
                      SELECT COUNT(*)
                      FROM RankedApps
                      WHERE rn BETWEEN 1 AND 10
                """.replaceAll("\\s+", " ").trim();

        String actualSql = SqlGenerateUtil.generateSelectCountSql(originalSql, DbType.mysql.name())
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expectedSql, actualSql);
    }

    @Test
    void sqlServerOuterApplyQueryTest() throws JSQLParserException {
        String originalSql = """
                    SELECT
                        a.id,
                        a.applicant,
                        log.log_content.value('(/action)[1]', 'NVARCHAR(50)') AS action
                    FROM access_control_apply_record a
                    OUTER APPLY (
                        SELECT TOP 1 log_content
                        FROM approval_log
                        WHERE apply_id = a.id
                        ORDER BY log_id DESC
                    ) AS log;
                """;

        String expectedSql = """
                   SELECT COUNT(*)
                        FROM access_control_apply_record a
                        OUTER APPLY (SELECT TOP 1 log_content
                            FROM approval_log
                            WHERE apply_id = a.id
                            ORDER BY log_id DESC) AS log
                """.replaceAll("\\s+", " ").trim();

        String actualSql = SqlGenerateUtil.generateSelectCountSql(originalSql, DbType.mysql.name())
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expectedSql, actualSql);
    }


    @Test
    void sqlServerWithRecursionQueryTest() throws JSQLParserException {
        String originalSql = """
                    WITH ApprovalHierarchy AS (
                            SELECT
                                id,
                                approver,
                                approve_time,
                                1 AS level
                            FROM access_control_approval_process
                            WHERE id = 1
                            UNION ALL
                            SELECT
                                acap.id,
                                acap.approver,
                                acap.approve_time,
                                ah.level + 1
                            FROM access_control_approval_process acap
                            INNER JOIN ApprovalHierarchy ah
                                ON acap.id = ah.id + 1
                        )
                        SELECT *
                        FROM ApprovalHierarchy;
                """;

        String expectedSql = """
                  WITH ApprovalHierarchy AS (SELECT
                                id,
                                approver,
                                approve_time,
                                1 AS level
                            FROM access_control_approval_process
                            WHERE id = 1
                            UNION ALL
                            SELECT
                                acap.id,
                                acap.approver,
                                acap.approve_time,
                                ah.level + 1
                            FROM access_control_approval_process acap
                            INNER JOIN ApprovalHierarchy ah
                                ON acap.id = ah.id + 1)
                        SELECT COUNT(*)
                        FROM ApprovalHierarchy
                """.replaceAll("\\s+", " ").trim();

        String actualSql = SqlGenerateUtil.generateSelectCountSql(originalSql, DbType.mysql.name())
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expectedSql, actualSql);
    }


    @Test
    void issue20250313test() {
        String sql = """
                -- Material issue
                SELECT
                  o.gwf7 工单号,out.btno 批次,out.newe 净重,out.qty 标重,out.chdt 日期,out.soty,out.dety
                FROM
                  stockoutdetailreport out
                RIGHT  JOIN obma o ON out.plan_biid=o.biid
                  WHERE o.whid='D2' AND o.outdety='JYCK' AND isnull(out.btno,'')<>''
                  -- AND o.biid='APLS22C01001'
                  ORDER BY out.chdt DESC;
                """;
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.sqlserver);

        if (stmt instanceof SQLSelectStatement selectStmt) {
            SQLSelectQueryBlock query = selectStmt.getSelect().getQueryBlock();
            query.getSelectList().clear();
            SQLAggregateExpr countExpr = new SQLAggregateExpr("COUNT");
            countExpr.addArgument(new SQLAllColumnExpr());
            query.addSelectItem(countExpr);
            query.setOrderBy(null);
            query.setLimit(null);
            query.setOffset(null);

        }
        String modifiedSql = SQLUtils.toSQLString(stmt, DbType.sqlserver);
        System.out.println("Modified SQL: " + modifiedSql);
    }

    @Test
    void testInvalidSql() {
        String invalidSql = "DELETE FROM table1";
        assertThrows(IllegalArgumentException.class, () ->
                SqlGenerateUtil.generateSelectCountSql(invalidSql, DbType.mysql.name())
        );
    }
}
