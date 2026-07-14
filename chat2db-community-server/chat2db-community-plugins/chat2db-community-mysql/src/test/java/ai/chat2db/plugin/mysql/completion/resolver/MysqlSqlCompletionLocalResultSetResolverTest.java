package ai.chat2db.plugin.mysql.completion.resolver;

import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionLocalResultSetResolverTest {

    @Test
    void resolvesClosedCteBeforeMainSelect() {
        List<MysqlSqlCompletionRelationScope.Relation> relations = resolveAtCaret(
                "with recent as (select id, status from orders) select * from recent r where r.{caret}");

        assertRelations(relations, localRelation("recent", null, List.of("id", "status")));
    }

    @Test
    void resolvesClosedCteBeforeMainSelectAcrossNewlines() {
        List<MysqlSqlCompletionRelationScope.Relation> relations = resolveAtCaret("""
                WITH paid_orders as(
                SELECT * FROM orders
                WHERE status='paid'
                )
                SELECT * from pa{caret}""");

        assertRelations(relations, localRelation("paid_orders", null, List.of()));
    }

    @Test
    void resolvesMultipleClosedCtesInOrder() {
        List<MysqlSqlCompletionRelationScope.Relation> relations = resolveAtCaret(
                "with a as (select id from orders), b as (select status from a) select * from b where {caret}");

        assertRelations(relations,
                localRelation("a", null, List.of("id")),
                localRelation("b", null, List.of("status")));
    }

    @Test
    void unfinishedCteDoesNotLeakIntoItsOwnDefinition() {
        List<MysqlSqlCompletionRelationScope.Relation> relations = resolveAtCaret(
                "with recent as (select st{caret} from orders) select * from recent");

        Assertions.assertTrue(relations.isEmpty());
    }

    @Test
    void nestedCteDoesNotLeakAfterDerivedTableCloses() {
        List<MysqlSqlCompletionRelationScope.Relation> relations = resolveAtCaret(
                "select * from (with nested as (select id from orders) select id from nested) d "
                        + "where {caret}");

        assertRelations(relations, localRelation("d", "d", List.of("id")));
        Assertions.assertTrue(relations.stream().noneMatch(relation -> "nested".equals(relation.table())));
    }

    @Test
    void resolvesDerivedTableWithAsAlias() {
        List<MysqlSqlCompletionRelationScope.Relation> relations = resolveAtCaret(
                "select * from (select id, amount total from orders) as d where d.{caret}");

        assertRelations(relations, localRelation("d", "d", List.of("id", "total")));
    }

    @Test
    void derivedTableRequiresAliasBeforeCursor() {
        List<MysqlSqlCompletionRelationScope.Relation> beforeAlias = resolveAtCaret(
                "select * from (select id from orders) {caret}d where d.id = 1");
        List<MysqlSqlCompletionRelationScope.Relation> afterAlias = resolveAtCaret(
                "select * from (select id from orders) d where d.{caret}");

        Assertions.assertTrue(beforeAlias.isEmpty());
        assertRelations(afterAlias, localRelation("d", "d", List.of("id")));
    }

    @Test
    void nestedDerivedTableKeepsOnlyOuterProjection() {
        List<MysqlSqlCompletionRelationScope.Relation> relations = resolveAtCaret(
                "select * from (select inner_q.order_id as public_id from "
                        + "(select o.id as order_id, o.status as hidden_status from orders o) inner_q) outer_q "
                        + "where outer_q.{caret}");

        assertRelations(relations, localRelation("outer_q", "outer_q", List.of("public_id")));
    }

    @Test
    void currentProjectionResolvesOnlyAfterResultClauses() {
        Optional<MysqlSqlCompletionRelationScope.Relation> selectScope = resolveCurrentProjectionAtCaret(
                "select id, status as state from orders order by {caret}");
        Optional<MysqlSqlCompletionRelationScope.Relation> projectionScope = resolveCurrentProjectionAtCaret(
                "select id, st{caret} from orders");
        Optional<MysqlSqlCompletionRelationScope.Relation> whereScope = resolveCurrentProjectionAtCaret(
                "select id, status as state from orders where {caret}");

        Assertions.assertEquals(Optional.of(localRelation("result", null, List.of("id", "state"))), selectScope);
        Assertions.assertTrue(projectionScope.isEmpty());
        Assertions.assertTrue(whereScope.isEmpty());
    }

    @Test
    void unionOrderResultUsesFirstSelectProjection() {
        Optional<MysqlSqlCompletionRelationScope.Relation> relation = resolveUnionOrderResultAtCaret(
                "select id, status as state from orders union select id, state from archived_orders order by {caret}");

        Assertions.assertEquals(Optional.of(localRelation("result", null, List.of("id", "state"))), relation);
    }

    @Test
    void unionOrderResultOnlyAppliesAfterUnionOrderBy() {
        Optional<MysqlSqlCompletionRelationScope.Relation> plainOrder = resolveUnionOrderResultAtCaret(
                "select id, status from orders order by {caret}");
        Optional<MysqlSqlCompletionRelationScope.Relation> beforeOrder = resolveUnionOrderResultAtCaret(
                "select id from orders union select id from archived_orders where {caret}");

        Assertions.assertTrue(plainOrder.isEmpty());
        Assertions.assertTrue(beforeOrder.isEmpty());
    }

    private List<MysqlSqlCompletionRelationScope.Relation> resolveAtCaret(String sqlWithCaret) {
        CaretSql caretSql = caretSql(sqlWithCaret);
        return MysqlSqlCompletionLocalResultSetResolver.resolve(tokens(caretSql.sql()), caretSql.cursor());
    }

    private Optional<MysqlSqlCompletionRelationScope.Relation> resolveCurrentProjectionAtCaret(String sqlWithCaret) {
        CaretSql caretSql = caretSql(sqlWithCaret);
        return MysqlSqlCompletionLocalResultSetResolver.resolveCurrentProjection(tokens(caretSql.sql()),
                caretSql.cursor());
    }

    private Optional<MysqlSqlCompletionRelationScope.Relation> resolveUnionOrderResultAtCaret(String sqlWithCaret) {
        CaretSql caretSql = caretSql(sqlWithCaret);
        return MysqlSqlCompletionLocalResultSetResolver.resolveUnionOrderResult(tokens(caretSql.sql()),
                caretSql.cursor());
    }

    private List<Token> tokens(String sql) {
        return MysqlSqlCompletionTokenUtil.defaultTokens(sql);
    }

    private CaretSql caretSql(String sqlWithCaret) {
        int cursor = sqlWithCaret.indexOf("{caret}");
        Assertions.assertTrue(cursor >= 0, "missing caret marker");
        return new CaretSql(sqlWithCaret.replace("{caret}", ""), cursor);
    }

    private void assertRelations(List<MysqlSqlCompletionRelationScope.Relation> relations,
                                 MysqlSqlCompletionRelationScope.Relation... expected) {
        Assertions.assertEquals(List.of(expected), relations);
    }

    private MysqlSqlCompletionRelationScope.Relation localRelation(String table,
                                                                  String alias,
                                                                  List<String> columns) {
        return MysqlSqlCompletionRelationScope.Relation.local(table, alias, columns);
    }

    private record CaretSql(String sql, int cursor) {
    }
}
