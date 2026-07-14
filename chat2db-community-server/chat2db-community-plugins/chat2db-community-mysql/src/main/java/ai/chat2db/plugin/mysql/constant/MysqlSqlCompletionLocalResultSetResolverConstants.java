package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.antlr.v4.runtime.Token;



public final class MysqlSqlCompletionLocalResultSetResolverConstants {

    public static final String RESULT_SET_RELATION = "result";

    private MysqlSqlCompletionLocalResultSetResolverConstants() {
    }
}
