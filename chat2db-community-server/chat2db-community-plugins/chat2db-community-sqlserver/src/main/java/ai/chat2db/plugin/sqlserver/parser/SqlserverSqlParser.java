package ai.chat2db.plugin.sqlserver.parser;

import ai.chat2db.spi.ISQLFileSplitter;
import ai.chat2db.spi.DefaultSQLFileSplitter;
import ai.chat2db.community.domain.api.enums.parser.FileSizeUnitEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.spi.parser.AbstractSqlParser;
import ai.chat2db.community.domain.api.service.task.ITaskProgressListener;
import ai.chat2db.community.domain.api.service.db.ISqlBatchHandler;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlLexer;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParser;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParserBaseVisitor;
import ai.chat2db.plugin.sqlserver.parser.error.strategy.SqlServerErrorStrategy;
import ai.chat2db.plugin.sqlserver.parser.visitor.SqlServerCreateTableVisitor;
import ai.chat2db.plugin.sqlserver.parser.visitor.SqlServerParserVisitor;
import ai.chat2db.plugin.sqlserver.parser.visitor.SqlServerSimpleParserVisitor;
import ai.chat2db.plugin.sqlserver.parser.visitor.SqlServerValidTableVisitor;
import ai.chat2db.spi.util.TokenUtil;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class SqlserverSqlParser extends AbstractSqlParser<TSqlParser, SqlServerDialect> {

    private static final Set<String> SQL_KEY_WORDS = Set.of(
            "GO", "CREATE", "ALTER", ";", "WITH", "MERGE",
            "DELETE", "INSERT", "SELECT", "UPDATE", "BEGIN",
            "PRINT", "CHECKPOINT", "END", "GET", "SEND",
            "WAITFOR", "CLOSE", "DEALLOCATE", "DECLARE",
            "FETCH", "OPEN", "EXECUTE", "KILL", "RECONFIGURE",
            "GRANT", "SET", "SETUSER", "SHUTDOWN", "COMMIT",
            "SAVE", "ROLLBACK", "USE", "DISABLE", "DROP",
            "ENABLE", "LOCK", "TRUNCATE", "DBCC", "BACKUP"
    );

    @Override
    public Set<String> getSqlStartKeywords() {
        return SQL_KEY_WORDS;
    }

    public SqlserverSqlParser() {
        super(new TSqlParser(null), new SqlServerDialect());
    }


    @Override
    protected ParseTree parserRoot(TSqlParser parser) {
        return parser.tsql_file();
    }

    @Override
    protected StatementContext createStatementContext(CommonTokenStream tokenStream, SqlTypeEnum sqlTypeEnum) {
        StatementContext statementContext = new StatementContext(tokenStream);
        statementContext.setRecoverSet(SqlServerErrorStrategy.recoverSet);
        TSqlParserBaseVisitor visitor = createVisitor(statementContext, sqlTypeEnum);
        statementContext.setVisitor(visitor);
        return statementContext;
    }

    private TSqlParserBaseVisitor createVisitor(StatementContext statementContext, SqlTypeEnum sqlTypeEnum) {
        return switch (sqlTypeEnum) {
            case SIMPLE -> new SqlServerSimpleParserVisitor(statementContext);
            case CREATE_TABLE -> new SqlServerCreateTableVisitor(statementContext);
            case VALID_TABLE -> new SqlServerValidTableVisitor(statementContext);
            default -> new SqlServerParserVisitor(statementContext);
        };
    }

    @Override
    protected Lexer createLexer(CharStream charStream) {
        return new TSqlLexer(charStream);
    }

    @Override
    public List<Statement> parserSqlScript(String sql) {
        Lexer lexer = getLexer(sql);
        CommonTokenStream tokenStream = createTokenStream(lexer);
        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();

        List<Statement> statements = new ArrayList<>();

        Token firstToken = null;
        Token lastToken = null;
        for (Token token : tokens) {
            int tokenType = token.getType();
            if (tokenType == Token.EOF) {
                break;
            }
            if (!TokenUtil.hasValuableText(token) || dialect.isComment(tokenType)) {
                continue;
            }
            if (tokenType == TSqlLexer.GO) {
                if (Objects.isNull(firstToken)) {
                    continue;
                }
                Statement statement = getStatement(tokenStream, firstToken, lastToken);
                if (firstToken.getType() == TSqlLexer.MERGE) {
                    String s = statement.getSql();
                    if (StringUtils.isNotBlank(s)) {
                        statement.setSql(s.trim() + ";");
                    }
                }
                statements.add(statement);
                firstToken = null;
                lastToken = null;
                continue;
            }

            if (Objects.isNull(firstToken)) {
                firstToken = token;
            }
            lastToken = token;
        }
        return statements;
    }

    public int parserSqlScript(File file,
                               ITaskProgressListener progressListener,
                               ISqlBatchHandler sqlBatchHandler) {
        Lexer lexer = new TSqlLexer(null);
        CharStream charStream = null;
        List<Token> currentTokens = new ArrayList<>(50);
        long bytesRead = 0L;
        int statementCount = 0;
        try (ISQLFileSplitter sqlServerSafeSQLFileSplitter = new DefaultSQLFileSplitter(1, FileSizeUnitEnum.KB, file, StandardCharsets.UTF_8)) {
            String content;
            while (StringUtils.isNotBlank(content = sqlServerSafeSQLFileSplitter.nextContent())) {
                if (CollectionUtils.isNotEmpty(currentTokens)) {
                    String leftTokens = currentTokens.stream().map(Token::getText).collect(Collectors.joining());
                    charStream = CharStreams.fromString(leftTokens + content);
                    currentTokens.clear();
                } else {
                    charStream = CharStreams.fromString(content);
                }
                lexer.setInputStream(charStream);
                UnbufferedTokenStream<Token> tokenStream = createUnbufferedTokenStream(lexer);
                Token firstToken = null;
                Token lastToken = null;
                while (tokenStream.LA(1) != Token.EOF) {
                    Token token = tokenStream.LT(1);
                    int tokenType = token.getType();
                    String text = token.getText();
                    bytesRead += text.getBytes().length;
                    if (!TokenUtil.hasValuableText(token) || dialect.isComment(tokenType)) {
                        if (Objects.nonNull(firstToken)) {
                            currentTokens.add(token);
                        }
                        tokenStream.consume();

                        continue;
                    }
                    if (tokenType == TSqlLexer.GO) {
                        if (Objects.isNull(firstToken)) {
                            tokenStream.consume();
                            continue;
                        }
                        Statement statement = getStatement(currentTokens);
                        sqlBatchHandler.handle(statement);
                        firstToken = null;
                        lastToken = null;
                        currentTokens.clear();
                        statementCount++;
                        progressListener.onProgress(bytesRead, statementCount);
                        tokenStream.consume();
                        continue;
                    }

                    if (Objects.isNull(firstToken)) {
                        firstToken = token;
                    }
                    lastToken = token;
                    currentTokens.add(token);
                    tokenStream.consume();
                }
            }
            if (CollectionUtils.isNotEmpty(currentTokens)) {
                Statement statement = getStatement(currentTokens);
                sqlBatchHandler.handle(statement);
                statementCount++;
                currentTokens.clear();
                progressListener.onProgress(bytesRead, statementCount);
            }
            sqlBatchHandler.flush();
            return statementCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

