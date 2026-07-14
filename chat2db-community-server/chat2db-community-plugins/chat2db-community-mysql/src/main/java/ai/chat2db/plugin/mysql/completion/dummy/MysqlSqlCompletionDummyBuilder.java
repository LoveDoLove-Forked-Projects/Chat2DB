package ai.chat2db.plugin.mysql.completion.dummy;

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


import static ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionDummyBuilderConstants.*;
public final class MysqlSqlCompletionDummyBuilder implements ISqlCompletionDummyBuilder {


    @Override
    public SqlCompletionDummySql build(SqlCompletionPipelineState state) {
        if (state == null) {
            return SqlCompletionDummySql.unchanged("", 0);
        }
        return build(state.window(), state.cursorContext());
    }

    public SqlCompletionDummySql build(SqlCompletionStatementWindow window, SqlCompletionCursorContext cursorContext) {
        if (Objects.isNull(window)) {
            return SqlCompletionDummySql.unchanged("", 0);
        }
        String sql = window.parseSql();
        int cursor = window.cursor();
        if (Objects.isNull(cursorContext) || !cursorContext.admitted()) {
            return SqlCompletionDummySql.unchanged(sql, cursor);
        }
        if (cursorContext.dotScoped() && cursorContext.prefix().isEmpty()) {
            return insert(sql, cursor, ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionTokenUtilConstants.COMPLETION_DUMMY_IDENTIFIER,
                    SqlCompletionDummyTypeEnum.DANGLING_DOT_IDENTIFIER);
        }
        if (!cursorContext.prefix().isEmpty()) {
            SqlCompletionDummySql dummySql = replace(sql, cursorContext.replaceStart(), cursorContext.replaceEnd(),
                    ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionTokenUtilConstants.COMPLETION_DUMMY_IDENTIFIER, SqlCompletionDummyTypeEnum.IDENTIFIER);
            if (canParse(dummySql.sql())) {
                return dummySql;
            }
            SqlCompletionDummySql dataTypeDummySql = replace(sql, cursorContext.replaceStart(),
                    cursorContext.replaceEnd(), DATA_TYPE_DUMMY,
                    SqlCompletionDummyTypeEnum.DATA_TYPE);
            if (canParse(dataTypeDummySql.sql())) {
                return dataTypeDummySql;
            }
            return dataTypeDummySql;
        }
        SqlCompletionDummySql dummySql = insert(sql, cursor, ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionTokenUtilConstants.COMPLETION_DUMMY_IDENTIFIER,
                SqlCompletionDummyTypeEnum.IDENTIFIER);
        if (canParse(dummySql.sql())) {
            return dummySql;
        }
        return insert(sql, cursor, DATA_TYPE_DUMMY, SqlCompletionDummyTypeEnum.DATA_TYPE);
    }

    private static SqlCompletionDummySql insert(String sql,
                                                int cursor,
                                                String dummy,
                                                SqlCompletionDummyTypeEnum type) {
        String value = Objects.toString(sql, "");
        int safeCursor = Math.max(0, Math.min(cursor, value.length()));
        String patchedSql = value.substring(0, safeCursor) + dummy + value.substring(safeCursor);
        return repair(patchedSql, safeCursor, type, safeCursor, dummy.length());
    }

    private static SqlCompletionDummySql replace(String sql,
                                                 int start,
                                                 int end,
                                                 String dummy,
                                                 SqlCompletionDummyTypeEnum type) {
        String value = Objects.toString(sql, "");
        int safeStart = Math.max(0, Math.min(start, value.length()));
        int safeEnd = Math.max(safeStart, Math.min(end, value.length()));
        String patchedSql = value.substring(0, safeStart) + dummy + value.substring(safeEnd);
        return repair(patchedSql, safeStart, type, safeStart, dummy.length());
    }

    private static SqlCompletionDummySql repair(String sql,
                                                int cursor,
                                                SqlCompletionDummyTypeEnum type,
                                                int insertedOffset,
        int insertedLength) {
        if (canParse(sql)) {
            return new SqlCompletionDummySql(sql, cursor, type.name(), insertedOffset, insertedLength);
        }
        int dummyEnd = Math.max(0, Math.min(insertedOffset + insertedLength, sql.length()));
        String prefixSql = closeParentheses(sql.substring(0, dummyEnd));
        if (canParse(prefixSql)) {
            return new SqlCompletionDummySql(prefixSql, Math.min(cursor, prefixSql.length()), type.name(),
                    insertedOffset, insertedLength);
        }
        return new SqlCompletionDummySql(sql, cursor, type.name(), insertedOffset, insertedLength);
    }

    private static boolean canParse(String sql) {
        try {
            MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(Objects.toString(sql, "")));
            lexer.removeErrorListeners();
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            MySqlParser parser = new MySqlParser(tokenStream);
            parser.removeErrorListeners();
            parser.setErrorHandler(new BailErrorStrategy());
            parser.root();
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private static String closeParentheses(String sql) {
        int open = 0;
        for (Token token : MysqlSqlCompletionTokenUtil.tokens(sql)) {
            if (!MysqlSqlCompletionTokenUtil.isDefaultToken(token)) {
                continue;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                open++;
            } else if (token.getType() == MySqlLexer.RR_BRACKET && open > 0) {
                open--;
            }
        }
        return sql + ")".repeat(open);
    }

}
