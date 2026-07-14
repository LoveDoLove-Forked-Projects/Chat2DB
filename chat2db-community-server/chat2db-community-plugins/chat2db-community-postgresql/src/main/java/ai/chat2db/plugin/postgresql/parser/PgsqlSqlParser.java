package ai.chat2db.plugin.postgresql.parser;

import ai.chat2db.spi.ISQLFileSplitter;
import ai.chat2db.spi.DefaultSQLFileSplitter;
import ai.chat2db.spi.constant.SQLConstants;
import ai.chat2db.community.domain.api.enums.parser.FileSizeUnitEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.spi.parser.AbstractSqlParser;
import ai.chat2db.community.domain.api.service.task.ITaskProgressListener;
import ai.chat2db.spi.IRuleManager;
import ai.chat2db.community.domain.api.service.db.ISqlBatchHandler;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLLexer;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParser;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParserBaseVisitor;
import ai.chat2db.plugin.postgresql.parser.error.strategy.PgsqlErrorStrategy;
import ai.chat2db.plugin.postgresql.parser.visitor.PgSqlSimpleParserVisitor;
import ai.chat2db.plugin.postgresql.parser.visitor.PgsqlCreateTableVisitor;
import ai.chat2db.plugin.postgresql.parser.visitor.PgsqlParserVisitor;
import ai.chat2db.plugin.postgresql.parser.visitor.PgsqlValidTableVisitor;
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
import java.util.stream.Collectors;

public class PgsqlSqlParser extends AbstractSqlParser<PostgreSQLParser, PgsqlDialect> {


    public PgsqlSqlParser() {
        super(new PostgreSQLParser(null), new PgsqlDialect());
    }

    @Override
    protected Lexer createLexer(CharStream charStream) {
        return new PostgreSQLLexer(charStream);
    }


    @Override
    protected ParseTree parserRoot(PostgreSQLParser parser) {
        return parser.root();
    }

    @Override
    protected StatementContext createStatementContext(CommonTokenStream tokenStream, SqlTypeEnum sqlTypeEnum) {
        StatementContext statementContext = new StatementContext(tokenStream);
        statementContext.setRecoverSet(PgsqlErrorStrategy.recoverSet);
        PostgreSQLParserBaseVisitor visitor = createVisitor(statementContext, sqlTypeEnum);
        statementContext.setVisitor(visitor);
        return statementContext;
    }


    private PostgreSQLParserBaseVisitor createVisitor(StatementContext statementContext, SqlTypeEnum sqlTypeEnum) {
        return switch (sqlTypeEnum) {
            case SIMPLE -> new PgSqlSimpleParserVisitor(statementContext);
            case CREATE_TABLE -> new PgsqlCreateTableVisitor(statementContext);
            case VALID_TABLE -> new PgsqlValidTableVisitor(statementContext);
            default -> new PgsqlParserVisitor(statementContext);
        };
    }

    @Override
    public List<Statement> parserSqlScript(String sql) {
        Lexer lexer = getLexer(sql);
        CommonTokenStream tokenStream = createTokenStream(lexer);
        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();

        List<Statement> statements = new ArrayList<>();
        IRuleManager ruleManager = dialect.getRuleManager();
        Token firstToken = null;
        Token lastToken = null;
        Token firstKeyword = null;
        Token lastKeyword = null;
        boolean encounteredBlockHeaderPrefix = false;
        SqlScriptBlockContext curBlock = null;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            int tokenType = token.getType();
            if (tokenType == Token.EOF) {
                break;
            }
            String currentTokenText = token.getText().trim();
            if (!TokenUtil.hasValuableText(token)
                    || dialect.isComment(tokenType)
                    || tokenType == PostgreSQLLexer.MetaCommand
                    || tokenType == PostgreSQLLexer.EndMetaCommand) {
                continue;
            }
            if (dialect.isStatementDelimiter(currentTokenText) && Objects.isNull(curBlock)) {
                if (Objects.isNull(firstToken)) {
                    continue;
                }
                Statement statement = getStatement(tokenStream, firstToken, lastToken);
                statements.add(statement);
                firstToken = null;
                lastToken = null;
                firstKeyword = null;
                lastKeyword = null;
                curBlock = null;
                encounteredBlockHeaderPrefix = false;
                continue;
            }
            if (Objects.isNull(firstToken)) {
                firstToken = token;
            }
            if (currentTokenText.length() == 1) {
                if (StringUtils.equalsAnyIgnoreCase(currentTokenText,
                        SQLConstants.OPEN_PARENTHESIS,
                        SQLConstants.OPEN_CURLY_BRACE,
                        SQLConstants.OPEN_SQUARE_BRACKET)) {
                    curBlock = new SqlScriptBlockContext(curBlock);
                }
                if (StringUtils.equalsAnyIgnoreCase(currentTokenText,
                        SQLConstants.CLOSE_PARENTHESIS,
                        SQLConstants.CLOSE_CURLY_BRACE,
                        SQLConstants.CLOSE_SQUARE_BRACKET)) {
                    if (Objects.nonNull(curBlock)) {
                        curBlock = curBlock.parent;
                    }
                }
            }
            if (dialect.isBlockToggleSymbol(currentTokenText)) {
                if (Objects.nonNull(curBlock) && currentTokenText.equals(curBlock.togglePattern)) {
                    curBlock = curBlock.parent;
                } else {
                    curBlock = new SqlScriptBlockContext(curBlock, currentTokenText);
                }
            }


            lastToken = token;
            if (dialect.isKeyword(currentTokenText)) {
                if (Objects.isNull(firstKeyword)) {
                    firstKeyword = token;
                }
                lastKeyword = token;
            }

        }

        return statements;
    }

    @Override
    public int parserSqlScript(File file, ITaskProgressListener progressListener, ISqlBatchHandler sqlBatchHandler) {
        long bytesRead = 0L;
        int statementCount = 0;
        String content = null;
        Lexer lexer = new PostgreSQLLexer(null);
        CharStream charStream = null;
        List<Token> currentTokens = new ArrayList<>(50);
        try (ISQLFileSplitter sqlFileSplitter = new DefaultSQLFileSplitter(10, FileSizeUnitEnum.MB, file, StandardCharsets.UTF_8)) {
            while (StringUtils.isNotBlank(content = sqlFileSplitter.nextContent())) {
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
                Token firstKeyword = null;
                Token lastKeyword = null;
                SqlScriptBlockContext curBlock = null;
                while (true) {
                    Token token = tokenStream.LT(1);
                    int tokenType = token.getType();
                    if (tokenType == Token.EOF) {
                        break;
                    }
                    String text = token.getText();
                    bytesRead += text.getBytes().length;
                    String currentTokenText = text.trim();
                    if (!TokenUtil.hasValuableText(token)
                            || dialect.isComment(tokenType)
                            || tokenType == PostgreSQLLexer.MetaCommand
                            || tokenType == PostgreSQLLexer.EndMetaCommand) {
                        if (Objects.nonNull(firstToken)) {
                            currentTokens.add(token);
                        }
                        tokenStream.consume();
                        continue;
                    }
                    if (dialect.isStatementDelimiter(currentTokenText) && Objects.isNull(curBlock)) {
                        if (Objects.isNull(firstToken)) {
                            tokenStream.consume();
                            continue;
                        }
                        Statement statement = getStatement(currentTokens);
                        sqlBatchHandler.handle(statement);
                        firstToken = null;
                        lastToken = null;
                        firstKeyword = null;
                        lastKeyword = null;
                        curBlock = null;
                        currentTokens.clear();
                        tokenStream.consume();
                        statementCount++;
                        progressListener.onProgress(bytesRead, statementCount);
                        continue;
                    }
                    if (Objects.isNull(firstToken)) {
                        firstToken = token;
                    }
                    currentTokens.add(token);
                    if (currentTokenText.length() == 1) {
                        if (StringUtils.equalsAnyIgnoreCase(currentTokenText,
                                SQLConstants.OPEN_PARENTHESIS,
                                SQLConstants.OPEN_CURLY_BRACE,
                                SQLConstants.OPEN_SQUARE_BRACKET)) {
                            curBlock = new SqlScriptBlockContext(curBlock);
                        }
                        if (StringUtils.equalsAnyIgnoreCase(currentTokenText,
                                SQLConstants.CLOSE_PARENTHESIS,
                                SQLConstants.CLOSE_CURLY_BRACE,
                                SQLConstants.CLOSE_SQUARE_BRACKET)) {
                            if (Objects.nonNull(curBlock)) {
                                curBlock = curBlock.parent;
                            }
                        }
                    }
                    if (dialect.isBlockToggleSymbol(currentTokenText)) {
                        if (Objects.nonNull(curBlock) && currentTokenText.equals(curBlock.togglePattern)) {
                            curBlock = curBlock.parent;
                        } else {
                            curBlock = new SqlScriptBlockContext(curBlock, currentTokenText);
                        }
                    }

                    lastToken = token;
                    if (dialect.isKeyword(currentTokenText)) {
                        if (Objects.isNull(firstKeyword)) {
                            firstKeyword = token;
                        }
                        lastKeyword = token;
                    }
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
