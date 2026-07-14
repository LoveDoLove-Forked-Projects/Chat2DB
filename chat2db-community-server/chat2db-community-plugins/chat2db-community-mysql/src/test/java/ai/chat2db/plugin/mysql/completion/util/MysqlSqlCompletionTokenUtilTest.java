package ai.chat2db.plugin.mysql.completion.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionTokenUtilTest {

    @Test
    void detectsUpdateSetAssignmentTargetBeforeEquals() {
        assertSetTarget("update user set {caret}");
        assertSetTarget("update user set na{caret}");
        assertSetTarget("update user set name = 'a', em{caret}");
    }

    @Test
    void doesNotTreatSetValueExpressionAsAssignmentTarget() {
        assertNotSetTarget("update user set name = {caret}");
        assertNotSetTarget("update user set name = concat({caret})");
        assertNotSetTarget("update user set name = 'a' where {caret}");
    }

    @Test
    void ignoresNestedEqualsWhileDetectingSetAssignmentTarget() {
        assertSetTarget("update user set name = if(id = 1, 'a', 'b'), em{caret}");
    }

    private static void assertSetTarget(String sqlWithCaret) {
        Assertions.assertTrue(isSetAssignmentTargetSlot(sqlWithCaret), sqlWithCaret);
    }

    private static void assertNotSetTarget(String sqlWithCaret) {
        Assertions.assertFalse(isSetAssignmentTargetSlot(sqlWithCaret), sqlWithCaret);
    }

    private static boolean isSetAssignmentTargetSlot(String sqlWithCaret) {
        int cursor = sqlWithCaret.indexOf("{caret}");
        Assertions.assertTrue(cursor >= 0, "missing caret marker");
        String sql = sqlWithCaret.replace("{caret}", "");
        return MysqlSqlCompletionTokenUtil.isSetAssignmentTargetSlot(sql, cursor);
    }
}
