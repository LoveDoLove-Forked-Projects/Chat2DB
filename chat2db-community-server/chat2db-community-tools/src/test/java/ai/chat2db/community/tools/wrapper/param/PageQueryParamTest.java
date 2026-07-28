package ai.chat2db.community.tools.wrapper.param;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Regression coverage for {@link PageQueryParam#andOrderBy(OrderBy)} NPEing
 * when invoked before any {@link PageQueryParam#orderBy(OrderBy)} call
 * (orderByList was null), and for orderBy() accumulating instead of replacing.
 */
class PageQueryParamTest {

    @Test
    void andOrderByBeforeOrderByDoesNotThrow() {
        PageQueryParam param = assertDoesNotThrow(() -> new PageQueryParam().andOrderBy(OrderBy.asc("x")));
        assertEquals(1, param.getOrderByList().size());
        assertEquals("x", param.getOrderByList().get(0).getOrderConditionName());
    }

    @Test
    void orderByThenAndOrderByAccumulates() {
        PageQueryParam param = new PageQueryParam()
            .orderBy(OrderBy.asc("a"))
            .andOrderBy(OrderBy.desc("b"));
        assertEquals(2, param.getOrderByList().size());
    }

    @Test
    void repeatedOrderByAccumulates() {
        PageQueryParam param = new PageQueryParam()
            .orderBy(OrderBy.asc("a"))
            .orderBy(OrderBy.asc("b"));
        assertEquals(2, param.getOrderByList().size());
    }
}
