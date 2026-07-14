package ai.chat2db.plugin.mysql.completion.resolver;

import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionLocalColumnScope;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionLocalColumnScopeResolverTest {

    @Test
    void resolvesColumnsDeclaredBeforeCurrentCreateTableDefinition() {
        MysqlSqlCompletionLocalColumnScope scope = resolveAtCaret("""
                create table orders (
                    id bigint,
                    amount decimal(10, 2),
                    primary key ({caret}
                )
                """);

        Assertions.assertEquals(new MysqlSqlCompletionLocalColumnScope(true, "orders",
                List.of("id", "amount")), scope);
    }

    @Test
    void ignoresTableConstraintsAndIndexesWhenCollectingDeclaredColumns() {
        MysqlSqlCompletionLocalColumnScope scope = resolveAtCaret("""
                create table orders (
                    id bigint,
                    primary key (id),
                    unique key uk_order_id (id),
                    amount decimal(10, 2),
                    check ({caret}
                )
                """);

        Assertions.assertEquals(new MysqlSqlCompletionLocalColumnScope(true, "orders",
                List.of("id", "amount")), scope);
    }

    @Test
    void doesNotExposeCurrentUnfinishedDefinitionAsDeclaredColumn() {
        MysqlSqlCompletionLocalColumnScope scope = resolveAtCaret("""
                create table orders (
                    id bigint,
                    na{caret}
                )
                """);

        Assertions.assertEquals(new MysqlSqlCompletionLocalColumnScope(true, "orders",
                List.of("id")), scope);
    }

    @Test
    void nestedParenthesesDoNotSplitColumnDefinitions() {
        MysqlSqlCompletionLocalColumnScope scope = resolveAtCaret("""
                create table orders (
                    id bigint,
                    amount decimal(10, 2) default (round(1.23, 1)),
                    check ({caret}
                )
                """);

        Assertions.assertEquals(new MysqlSqlCompletionLocalColumnScope(true, "orders",
                List.of("id", "amount")), scope);
    }

    @Test
    void outsideCreateTableDefinitionIsNotApplicable() {
        MysqlSqlCompletionLocalColumnScope beforeDefinitions = resolveAtCaret(
                "create table orders {caret} (id bigint)");
        MysqlSqlCompletionLocalColumnScope afterDefinitions = resolveAtCaret(
                "create table orders (id bigint) {caret}");
        MysqlSqlCompletionLocalColumnScope plainSelect = resolveAtCaret(
                "select {caret} from orders");

        Assertions.assertFalse(beforeDefinitions.applies());
        Assertions.assertFalse(afterDefinitions.applies());
        Assertions.assertFalse(plainSelect.applies());
    }

    private MysqlSqlCompletionLocalColumnScope resolveAtCaret(String sqlWithCaret) {
        int cursor = sqlWithCaret.indexOf("{caret}");
        Assertions.assertTrue(cursor >= 0, "missing caret marker");
        return MysqlSqlCompletionLocalColumnScopeResolver.resolve(sqlWithCaret.replace("{caret}", ""), cursor);
    }
}
