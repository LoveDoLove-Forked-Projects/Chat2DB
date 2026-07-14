package ai.chat2db.spi.parser;

import ai.chat2db.spi.IRuleManager;
import ai.chat2db.spi.ISQLParser;

import ai.chat2db.spi.constant.SQLConstants;
import ai.chat2db.spi.ISQLDialect;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.StatementValidTypeEnum;
import ai.chat2db.spi.parser.error.listener.BaseSyntaxErrorListener;
import ai.chat2db.spi.parser.error.strategy.BaseErrorStrategy;
import ai.chat2db.community.domain.api.model.parser.position.TokenPosition;
import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.statement.create.CreateTableStatement;
import ai.chat2db.spi.util.IntervalUtil;
import ai.chat2db.spi.util.TokenUtil;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractSqlParser<P extends Parser, D extends ISQLDialect> implements ISQLParser {

    protected final P parser;
    protected final D dialect;

    public AbstractSqlParser(P parser, D dialect) {
        this.dialect = dialect;
        this.parser = parser;
    }

    protected abstract Lexer createLexer(CharStream charStream);

    protected CommonTokenStream createTokenStream(Lexer lexer) {
        return new CommonTokenStream(lexer);
    }

    protected UnbufferedTokenStream<Token> createUnbufferedTokenStream(Lexer lexer) {
        return new UnbufferedTokenStream<>(lexer);
    }

    protected abstract ParseTree parserRoot(P parser);

    protected abstract StatementContext createStatementContext(CommonTokenStream tokenStream, SqlTypeEnum sqlTypeEnum);


    protected Lexer getLexer(String sql) {
        CodePointCharStream charStream = CharStreams.fromString(sql);
        return createLexer(charStream);
    }

    protected Lexer getLexer(File file) {
        CharStream charStream;
        try {
            charStream = CharStreams.fromPath(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("can not read file", e);
        }
        return createLexer(charStream);
    }

    @Override
    public Set<String> getSqlStartKeywords() {
        return dialect.getSqlStartKeywords();
    }


    @Override
    public SqlParserResponse parserStatements(String sql) {
        Lexer lexer = getLexer(sql);
        lexer.removeErrorListeners();
        CommonTokenStream tokenStream = createTokenStream(lexer);
        tokenStream.fill();
        parser.setTokenStream(tokenStream);
        BaseSyntaxErrorListener baseSyntaxErrorListener = new BaseSyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(baseSyntaxErrorListener);
        BaseErrorStrategy baseErrorStrategy = new BaseErrorStrategy();
        parser.setErrorHandler(baseErrorStrategy);
        StatementContext statementContext = createStatementContext(tokenStream, SqlTypeEnum.OTHER);
        List<Statement> statements = collectStatements(parser, statementContext);
        return SqlParserResponse.builder().statements(statements).syntaxErrors(baseSyntaxErrorListener.getErrorMessages()).build();
    }


    @Override
    public SqlParserResponse simpleParserStatements(String sql) {
        Lexer lexer = getLexer(sql);
        CommonTokenStream tokenStream = createTokenStream(lexer);
        tokenStream.fill();
        parser.setTokenStream(tokenStream);
        parser.removeErrorListeners();
        BaseErrorStrategy baseErrorStrategy = new BaseErrorStrategy();
        parser.setErrorHandler(baseErrorStrategy);
        StatementContext statementContext = createStatementContext(tokenStream, SqlTypeEnum.SIMPLE);
        ParseTree parseTree = parserRoot(parser);
        AbstractParseTreeVisitor visitor = statementContext.getVisitor();
        visitor.visit(parseTree);
        return SqlParserResponse.builder().statements(statementContext.getStatements()).build();
    }

    @Override
    public SqlParserResponse validTableStatements(String sql) {
        Lexer lexer = getLexer(sql);
        CommonTokenStream tokenStream = createTokenStream(lexer);
        tokenStream.fill();
        parser.setTokenStream(tokenStream);
        parser.removeErrorListeners();
        BaseErrorStrategy baseErrorStrategy = new BaseErrorStrategy();
        parser.setErrorHandler(baseErrorStrategy);
        StatementContext statementContext = createStatementContext(tokenStream, SqlTypeEnum.VALID_TABLE);
        ParseTree parseTree = parserRoot(parser);
        AbstractParseTreeVisitor visitor = statementContext.getVisitor();
        visitor.visit(parseTree);
        return SqlParserResponse.builder().statements(statementContext.getStatements()).build();
    }

    @Override
    public CreateTableStatement parserCreateTableStatement(String sql) {
        Lexer lexer = getLexer(sql);
        CommonTokenStream tokenStream = createTokenStream(lexer);
        tokenStream.fill();
        parser.setTokenStream(tokenStream);
        BaseErrorStrategy baseErrorStrategy = new BaseErrorStrategy();
        parser.setErrorHandler(baseErrorStrategy);
        StatementContext statementContext = createStatementContext(tokenStream, SqlTypeEnum.CREATE_TABLE);
        List<Statement> statements = collectStatements(parser, statementContext);
        List<Statement> createTableStatements = statements.stream().filter(s -> SqlTypeEnum.CREATE_TABLE.name().equals(s.getType())).toList();
        if (createTableStatements.size() == 1) {
            return (CreateTableStatement) createTableStatements.get(0);
        }
        return null;
    }

    @Override
    public boolean isSelect(String sql) {
        Lexer lexer = getLexer(sql);
        CommonTokenStream tokenStream = createTokenStream(lexer);
        tokenStream.fill();
        parser.setTokenStream(tokenStream);
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());
        StatementContext statementContext = createStatementContext(tokenStream, SqlTypeEnum.SIMPLE);
        List<Statement> statements = collectStatements(parser, statementContext);
        if (CollectionUtils.isNotEmpty(statements)) {
            if (statements.size() != 1) {
                return false;
            } else {
                return statements.get(0).getType().equals(SqlTypeEnum.SELECT.name());
            }
        }
        return false;
    }


    @Override
    public List<Token> getAllTokens(String sql) {
        Lexer lexer = getLexer(sql);
        CommonTokenStream tokenStream = createTokenStream(lexer);
        tokenStream.fill();
        return tokenStream.getTokens();
    }

    @Override
    public List<Token> getAllTokensOnDefault(String sql) {
        return TokenUtil.getParserRuleTokensOnDefault(getAllTokens(sql));
    }

    @Override
    public Map<TokenPosition, Token> getTokenPositionMap(String sql) {
        return TokenUtil.getTokenPositionMap(getAllTokensOnDefault(sql));
    }

    protected List<Statement> collectStatements(P parser, StatementContext statementContext) {
        CommonTokenStream commonTokenStream = statementContext.getCommonTokenStream();
        AbstractParseTreeVisitor visitor = statementContext.getVisitor();
        boolean errorOccurred = false;
        HashSet<Interval> succeededIntervals = new HashSet<>();
        while (commonTokenStream.LA(1) != Token.EOF) {
            try {
                ParseTree parseTree = parserRoot(parser);
                visitor.visit(parseTree);
                succeededIntervals.add(parseTree.getSourceInterval());
                break;
            } catch (ParseCancellationException e) {
                errorOccurred = true;
                RecognitionException cause = (RecognitionException) e.getCause();
                RuleContext ctx = cause.getCtx();
                int childCount = ctx.getChildCount();
                if (childCount > 0) {
                    succeededIntervals.add(ctx.getSourceInterval());
                    visitor.visit(ctx);
                }
                Token offendingToken = cause.getOffendingToken();
                if (Objects.nonNull(offendingToken)) {
                    commonTokenStream.seek(offendingToken.getTokenIndex() + 1);
                } else {
                    commonTokenStream.consume();
                }
                parser.setTokenStream(commonTokenStream);
            }
        }
        List<Statement> statements = statementContext.getStatements();
        if (errorOccurred) {
            int lastIndex = 0;
            List<List<Token>> batches = new ArrayList<>();
            if (CollectionUtils.isEmpty(succeededIntervals)) {
                processTokens(commonTokenStream.getTokens(), batches, statements);
            } else {
                List<Interval> sortIntervals = IntervalUtil.sortIntervals(succeededIntervals);
                for (Interval succeededInterval : sortIntervals) {
                    int start = succeededInterval.a;
                    int stop = succeededInterval.b;
                    if (start == lastIndex) {
                        lastIndex = stop + 1;
                        continue;
                    }
                    List<Token> tokens = commonTokenStream.getTokens(lastIndex, start - 1);
                    if (CollectionUtils.isNotEmpty(tokens)) {
                        processTokens(tokens, batches, statements);
                        batches.clear();
                    }
                    lastIndex = stop + 1;
                }
                int lastSucceededTokenIndex = sortIntervals.get(sortIntervals.size() - 1).b;
                int lastTokenIndex = commonTokenStream.size() - 1;
                if (lastTokenIndex - lastSucceededTokenIndex > 2) {
                    List<Token> tokens = commonTokenStream.getTokens(lastSucceededTokenIndex + 1, lastTokenIndex);
                    if (CollectionUtils.isNotEmpty(tokens)) {
                        processTokens(tokens, batches, statements);
                        batches.clear();
                    }
                }
                sortIntervals.clear();
            }

        }
        return statements;
    }
    private void processTokens(List<Token> tokens, List<List<Token>> batches, List<Statement> statements) {
        List<Token> currentBatch = new ArrayList<>();
        StringBuilder errorSqlBuilder = new StringBuilder(100);

        for (Token token : tokens) {
            if (token.getType() == Token.EOF) {
                addBatchIfNotEmpty(currentBatch, batches);
                break;
            }
            if (dialect.isStatementDelimiter(token.getText())) {
                addBatchIfNotEmpty(currentBatch, batches);
                continue;
            }
            currentBatch.add(token);
        }

        addBatchIfNotEmpty(currentBatch, batches);

        for (List<Token> batch : batches) {
            String errorSql = buildErrorSql(batch, errorSqlBuilder);
            if (StringUtils.isBlank(errorSql) || dialect.isStatementDelimiter(errorSql)) {
                continue;
            }
            Statement statement = new Statement(errorSql);
            statement.setOriginalSql(errorSql);
            statement.setStatementType(StatementValidTypeEnum.INVALID.name());
            statement.setFirstToken(batch.get(0));
            statement.setLastToken(batch.get(batch.size() - 1));
            statements.add(statement);
        }
    }
    private void addBatchIfNotEmpty(List<Token> currentBatch, List<List<Token>> batches) {
        if (!currentBatch.isEmpty()) {
            batches.add(new ArrayList<>(currentBatch));
            currentBatch.clear();
        }
    }
    private String buildErrorSql(List<Token> batch, StringBuilder errorSqlBuilder) {
        errorSqlBuilder.setLength(0);
        for (Token token : batch) {
            errorSqlBuilder.append(token.getText());
        }
        return errorSqlBuilder.toString().trim();
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
        boolean isDelimiter = false;
        boolean changeDelimiterMode = false;
        String udfDelimiter = null;
        boolean encounteredBlockHeaderPrefix = false;
        SqlScriptBlockContext curBlock = null;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            int tokenType = token.getType();
            if (tokenType == Token.EOF) {
                break;
            }
            String currentTokenText = token.getText().trim();
            if (!TokenUtil.hasValuableText(token)) {
                continue;
            } else if (dialect.isComment(tokenType)) {
                if (changeDelimiterMode && Objects.nonNull(lastKeyword) && dialect.isSetDelimiter(lastKeyword.getText())) {
                    udfDelimiter = currentTokenText;
                    lastKeyword = null;
                }
                continue;
            }
            if (dialect.isSetDelimiter(currentTokenText)) {
                if (changeDelimiterMode) {
                    changeDelimiterMode = false;
                    udfDelimiter = null;
                    continue;
                }
                changeDelimiterMode = true;
                udfDelimiter = null;
                lastKeyword = token;
                continue;
            } else if (changeDelimiterMode) {
                if (StringUtils.isBlank(udfDelimiter)) {
                    if (Objects.nonNull(lastKeyword) && dialect.isSetDelimiter(lastKeyword.getText())) {
                        if (TokenUtil.noLetterDigit(token)) {
                            udfDelimiter = currentTokenText;
                            continue;
                        }
                    } else {
                        lastKeyword = null;
                    }
                } else {
                    if (currentTokenText.equals(udfDelimiter)) {
                        udfDelimiter = null;
                        continue;
                    }
                }
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
                changeDelimiterMode = false;
                udfDelimiter = null;
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
            if (dialect.isBlockHeaderPrefix(currentTokenText)) {
                encounteredBlockHeaderPrefix = true;
                lastToken = token;
                firstKeyword = token;
                lastKeyword = token;
                continue;
            }

            if (encounteredBlockHeaderPrefix) {
                if (dialect.isDependentBlockHeader(currentTokenText)) {
                    boolean isBlockHeader = true;
                    if (Objects.nonNull(ruleManager)) {
                        isBlockHeader = ruleManager.matchRules(tokens, i);
                    }
                    if (isBlockHeader) {
                        curBlock = new SqlScriptBlockContext(curBlock, true);
                        lastToken = token;
                        lastKeyword = token;
                        encounteredBlockHeaderPrefix = false;
                    }
                    continue;
                }
            } else {
                if (dialect.isIndependentBlockHeader(currentTokenText) && Objects.isNull(curBlock)) {
                    curBlock = new SqlScriptBlockContext(null, true);
                    if (Objects.isNull(firstKeyword)) {
                        firstKeyword = token;
                    }
                    lastToken = token;
                    lastKeyword = token;
                    continue;
                }
                if (Objects.nonNull(curBlock)
                        && dialect.isPackageBodyInnerBlockHeader(currentTokenText)
                        && Objects.nonNull(ruleManager)
                        && ruleManager.matchRules(tokens, i)) {
                    curBlock = new SqlScriptBlockContext(curBlock, true);
                    lastToken = token;
                    lastKeyword = token;
                    continue;

                }
            }
            if (dialect.isBlockToggleSymbol(currentTokenText)) {
                if (Objects.nonNull(curBlock) && currentTokenText.equals(curBlock.togglePattern)) {
                    curBlock = curBlock.parent;
                } else {
                    curBlock = new SqlScriptBlockContext(curBlock, currentTokenText);
                }
            } else if (dialect.isBlockBegin(currentTokenText)) {
                if (Objects.nonNull(lastToken) && dialect.isBlockEnd(lastToken.getText())) {
                    lastToken = token;
                    continue;
                } else {
                    boolean isBlock = true;
                    if (Objects.nonNull(ruleManager)) {
                        isBlock = ruleManager.matchRules(tokens, i);
                    }
                    if (isBlock) {
                        if (Objects.nonNull(curBlock) && curBlock.isHeader) {
                            curBlock = curBlock.parent;
                        }
                        curBlock = new SqlScriptBlockContext(curBlock);
                    }
                }
            } else if (dialect.isBlockEnd(currentTokenText)) {
                if (Objects.nonNull(curBlock)) {
                    curBlock = curBlock.parent;
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


    protected static Statement getStatement(CommonTokenStream tokenStream, Token firstToken, Token previousToken) {
        String sqlText = tokenStream.getText(firstToken, previousToken);
        Statement statement = new Statement();
        statement.setOriginalSql(sqlText);
        statement.setSql(sqlText);
        statement.setFirstToken(firstToken);
        statement.setLastToken(previousToken);
        return statement;
    }


    protected static Statement getStatement(List<Token> tokens) {
        String sqlText = tokens.stream().map(Token::getText).collect(Collectors.joining());
        Statement statement = new Statement();
        statement.setOriginalSql(sqlText);
        statement.setSql(sqlText);
        statement.setFirstToken(tokens.get(0));
        statement.setLastToken(tokens.get(tokens.size() - 1));
        return statement;
    }


    protected static class SqlScriptBlockContext {

        public final SqlScriptBlockContext parent;
        public final String type;
        public final String togglePattern;
        public boolean isHeader;


        public SqlScriptBlockContext(SqlScriptBlockContext parent) {
            this.parent = parent;
            this.type = null;
            this.togglePattern = null;
            this.isHeader = false;
        }


        public SqlScriptBlockContext(SqlScriptBlockContext parent, boolean isHeader) {
            this.parent = parent;
            this.type = null;
            this.togglePattern = null;
            this.isHeader = isHeader;
        }


        public SqlScriptBlockContext(SqlScriptBlockContext parent, String togglePattern) {
            this.parent = parent;
            this.type = null;
            this.togglePattern = togglePattern;
            this.isHeader = false;
        }

        public boolean isRoot() {
            return parent == null;
        }

        public SqlScriptBlockContext getRoot() {
            SqlScriptBlockContext current = this;
            while (current.parent != null) {
                current = current.parent;
            }
            return current;
        }

        @Override
        public String toString() {
            return "ScriptBlockInfo{" + "type='" + type + '\'' + ", isHeader=" + isHeader + ", togglePattern='" + togglePattern + '\'' + '}';
        }
    }
}
