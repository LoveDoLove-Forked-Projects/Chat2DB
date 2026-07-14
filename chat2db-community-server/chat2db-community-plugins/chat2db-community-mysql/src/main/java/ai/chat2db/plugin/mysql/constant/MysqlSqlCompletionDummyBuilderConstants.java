package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionDummyTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionDummySql;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import ai.chat2db.spi.ISqlCompletionDummyBuilder;
import java.util.Objects;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;



public final class MysqlSqlCompletionDummyBuilderConstants {

    public static final String DATA_TYPE_DUMMY = "INT";

    private MysqlSqlCompletionDummyBuilderConstants() {
    }
}
