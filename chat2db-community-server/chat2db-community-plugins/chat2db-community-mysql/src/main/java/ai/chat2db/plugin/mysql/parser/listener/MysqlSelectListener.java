package ai.chat2db.plugin.mysql.parser.listener;

import ai.chat2db.community.domain.api.model.parser.info.ColumnInfo;
import ai.chat2db.community.domain.api.model.parser.info.TableInfo;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.mysql.parser.base.MySqlParserBaseListener;
import ai.chat2db.plugin.mysql.parser.util.MysqlStringUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class MysqlSelectListener extends MySqlParserBaseListener {

    private final StatementContext context;

    private Map<TableInfo, String> tableAliasMap = new HashMap<>();

    private Map<ColumnInfo, String> columnAliasMap = new HashMap<>();

    public MysqlSelectListener(StatementContext context) {
        this.context = context;
    }

    public void parserSelectStatement(ParseTree parseTree) {
        tableAliasMap = new HashMap<>();
        columnAliasMap = new HashMap<>();
        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        parseTreeWalker.walk(this, parseTree);
    }


    @Override
    public void exitSimpleSelect(MySqlParser.SimpleSelectContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        currentStatement.setTableAliasMap(tableAliasMap);
        currentStatement.setColumnAliasMap(columnAliasMap);

    }

    @Override
    public void exitParenthesisSelect(MySqlParser.ParenthesisSelectContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        currentStatement.setTableAliasMap(tableAliasMap);
        currentStatement.setColumnAliasMap(columnAliasMap);

    }

    @Override
    public void exitUnionSelect(MySqlParser.UnionSelectContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        currentStatement.setTableAliasMap(tableAliasMap);
        currentStatement.setColumnAliasMap(columnAliasMap);

    }

    @Override
    public void exitUnionParenthesisSelect(MySqlParser.UnionParenthesisSelectContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        currentStatement.setTableAliasMap(tableAliasMap);
        currentStatement.setColumnAliasMap(columnAliasMap);

    }

    @Override
    public void exitWithLateralStatement(MySqlParser.WithLateralStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        currentStatement.setTableAliasMap(tableAliasMap);
        currentStatement.setColumnAliasMap(columnAliasMap);
    }

    @Override
    public void enterAtomTableItem(MySqlParser.AtomTableItemContext ctx) {
        MySqlParser.TableNameContext tableNameContext = ctx.tableReferenceName().tableName();
        if (tableNameContext == null) {
            return;
        }
        String tableName = tableNameContext.getText();
        String[] splits = tableName.split("\\.");
        TableInfo tableInfo;
        String alias = null;
        if (splits.length == 2) {
            tableInfo = TableInfo.builder()
                    .database(MysqlStringUtil.removeQuote(splits[0]))
                    .table(MysqlStringUtil.removeQuote(splits[1]))
                    .build();
        } else {
            tableInfo = TableInfo.builder().table(MysqlStringUtil.removeQuote(splits[0]))
                    .build();
        }
        MySqlParser.UidContext uid = ctx.aliasDeclarationName() == null ? null : ctx.aliasDeclarationName().uid();
        if (uid != null) {
            alias = MysqlStringUtil.removeQuote(uid.getText());
        }
        tableAliasMap.put(tableInfo, alias);
    }


    @Override
    public void enterSelectColumnElement(MySqlParser.SelectColumnElementContext ctx) {
        ColumnInfo columnInfo;
        String alias = null;
        MySqlParser.FullColumnNameContext fullColumnNameContext = ctx.columnReferenceName().fullColumnName();
        if (fullColumnNameContext == null) {
            return;
        }

        String text = fullColumnNameContext.getText();
        String[] splits = text.split("\\.");
        if (splits.length == 3) {
            columnInfo = ColumnInfo.builder()
                    .database(MysqlStringUtil.removeQuote(splits[0]))
                    .table(MysqlStringUtil.removeQuote(splits[1]))
                    .column(MysqlStringUtil.removeQuote(splits[2]))
                    .build();
        } else if (splits.length == 2) {
            columnInfo = ColumnInfo.builder()
                    .table(MysqlStringUtil.removeQuote(splits[0]))
                    .column(MysqlStringUtil.removeQuote(splits[1]))
                    .build();
        } else {
            columnInfo = ColumnInfo.builder()
                    .column(MysqlStringUtil.removeQuote(splits[0]))
                    .build();
        }

        MySqlParser.UidContext uid = ctx.aliasDeclarationName() == null ? null : ctx.aliasDeclarationName().uid();
        if (uid != null) {
            alias = MysqlStringUtil.removeQuote(uid.getText());
        }

        columnAliasMap.put(columnInfo, alias);
    }

    @Override
    public void enterSelectElements(MySqlParser.SelectElementsContext ctx) {
        Token star = ctx.star;
        if (star != null) {
            columnAliasMap.put(ColumnInfo.builder().column("*").build(), null);
        }
    }

    @Override
    public void enterSelectFunctionElement(MySqlParser.SelectFunctionElementContext ctx) {
        MySqlParser.FunctionCallContext functionCallContext = ctx.functionCall();
        if (functionCallContext == null) {
            return;
        }
        String functionCallContextText = functionCallContext.getText();
        String content = extractDeepestContent(functionCallContextText);
        if (content == null) {
            return;
        }
        String[] splits = content.split("\\.");
        ColumnInfo columnInfo;
        if (splits.length == 3) {
            columnInfo = ColumnInfo.builder()
                    .database(MysqlStringUtil.removeQuote(splits[0]))
                    .table(MysqlStringUtil.removeQuote(splits[1]))
                    .column(MysqlStringUtil.removeQuote(splits[2]))
                    .build();
        } else if (splits.length == 2) {
            columnInfo = ColumnInfo.builder()
                    .table(MysqlStringUtil.removeQuote(splits[0]))
                    .column(MysqlStringUtil.removeQuote(splits[1]))
                    .build();
        } else {
            columnInfo = ColumnInfo.builder()
                    .column(MysqlStringUtil.removeQuote(splits[0]))
                    .build();
        }
        String alias = null;
        MySqlParser.UidContext uid = ctx.aliasDeclarationName() == null ? null : ctx.aliasDeclarationName().uid();
        if (uid != null) {
            alias = MysqlStringUtil.removeQuote(uid.getText());
        }
        columnAliasMap.put(columnInfo, alias);

    }

    @Override
    public void enterSelectStarElement(MySqlParser.SelectStarElementContext ctx) {
        MySqlParser.FullIdContext fullIdContext = ctx.fullId();
        String database = null, table = null;
        if (Objects.nonNull(fullIdContext)) {
            String text = fullIdContext.getText();
            String[] split = text.split("\\.");
            int length = split.length;
            if (length == 2) {
                database = MysqlStringUtil.removeQuote(split[0]);
            }
            table = MysqlStringUtil.removeQuote(split[length - 1]);
        }
        columnAliasMap.put(
                ColumnInfo.builder()
                        .database(database)
                        .table(table)
                        .column("*")
                        .build(), null
        );
    }

    @Override
    public void enterSelectExpressionElement(MySqlParser.SelectExpressionElementContext ctx) {
    }

    public static String extractDeepestContent(String input) {
        Stack<Integer> stack = new Stack<>();
        int deepestStart = -1, deepestEnd = -1, maxDepth = 0, currentDepth = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                stack.push(i);
                currentDepth++;
                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                    deepestStart = i;
                }
            } else if (input.charAt(i) == ')') {
                if (!stack.isEmpty()) {
                    int start = stack.pop();
                    currentDepth--;
                    if (start == deepestStart) {
                        deepestEnd = i;
                    }
                }
            }
        }
        if (deepestStart != -1 && deepestEnd != -1) {
            return input.substring(deepestStart + 1, deepestEnd).trim();
        }
        return null;
    }


}
