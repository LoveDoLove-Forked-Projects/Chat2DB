package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.mysql.parser.base.MySqlLexer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;



public final class MysqlSqlCompletionTokenUtilConstants {

    public static final String COMPLETION_DUMMY_IDENTIFIER = "__chat2db_completion_dummy";

    private MysqlSqlCompletionTokenUtilConstants() {
    }
}
