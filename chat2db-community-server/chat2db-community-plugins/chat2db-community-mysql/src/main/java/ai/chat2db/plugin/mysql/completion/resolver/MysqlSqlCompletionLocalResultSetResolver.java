package ai.chat2db.plugin.mysql.completion.resolver;

import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.antlr.v4.runtime.Token;


import static ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionLocalResultSetResolverConstants.*;
final class MysqlSqlCompletionLocalResultSetResolver {


    private static final Set<Integer> SELECT_BOUNDARY_TOKENS = Set.of(
            MySqlLexer.FROM,
            MySqlLexer.WHERE,
            MySqlLexer.GROUP,
            MySqlLexer.HAVING,
            MySqlLexer.WINDOW,
            MySqlLexer.ORDER,
            MySqlLexer.LIMIT,
            MySqlLexer.UNION);

    private MysqlSqlCompletionLocalResultSetResolver() {
    }

    static List<MysqlSqlCompletionRelationScope.Relation> resolve(List<Token> tokens, int cursor) {
        if (tokens == null || tokens.isEmpty()) {
            return List.of();
        }
        List<MysqlSqlCompletionRelationScope.Relation> relations = new ArrayList<>();
        relations.addAll(resolveCtes(tokens, cursor));
        relations.addAll(resolveDerivedTables(tokens, cursor));
        return relations;
    }

    static Optional<MysqlSqlCompletionRelationScope.Relation> resolveUnionOrderResult(List<Token> tokens, int cursor) {
        if (tokens == null || tokens.isEmpty()) {
            return Optional.empty();
        }
        int depth = MysqlSqlCompletionTokenUtil.depthAtOffset(tokens, cursor);
        int orderIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBeforeAtDepth(tokens, cursor, depth,
                MySqlLexer.ORDER);
        int unionIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBeforeAtDepth(tokens, cursor, depth,
                MySqlLexer.UNION);
        if (orderIndex < 0 || unionIndex < 0 || orderIndex < unionIndex) {
            return Optional.empty();
        }
        int selectIndex = MysqlSqlCompletionTokenUtil.firstDefaultIndexBeforeAtDepth(tokens, unionIndex, depth,
                MySqlLexer.SELECT);
        if (selectIndex < 0) {
            return Optional.empty();
        }
        List<String> columns = selectProjectionColumns(tokens, selectIndex, unionIndex);
        return columns.isEmpty() ? Optional.empty()
                : Optional.of(MysqlSqlCompletionRelationScope.Relation.local(RESULT_SET_RELATION, null, columns));
    }

    static Optional<MysqlSqlCompletionRelationScope.Relation> resolveCurrentProjection(List<Token> tokens, int cursor) {
        if (tokens == null || tokens.isEmpty()) {
            return Optional.empty();
        }
        int depth = MysqlSqlCompletionTokenUtil.depthAtOffset(tokens, cursor);
        int selectIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBeforeAtDepth(tokens, cursor, depth,
                MySqlLexer.SELECT);
        if (selectIndex < 0) {
            return Optional.empty();
        }
        int clauseIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBeforeAtDepth(tokens, cursor, depth,
                MySqlLexer.GROUP, MySqlLexer.HAVING, MySqlLexer.ORDER);
        if (clauseIndex < selectIndex) {
            return Optional.empty();
        }
        int projectionEnd = projectionEnd(tokens, selectIndex + 1, cursor);
        if (projectionEnd >= clauseIndex) {
            return Optional.empty();
        }
        List<String> columns = selectProjectionColumns(tokens, selectIndex, cursor);
        return columns.isEmpty() ? Optional.empty()
                : Optional.of(MysqlSqlCompletionRelationScope.Relation.local(RESULT_SET_RELATION, null, columns));
    }

    private static List<MysqlSqlCompletionRelationScope.Relation> resolveCtes(List<Token> tokens, int cursor) {
        int depth = MysqlSqlCompletionTokenUtil.depthAtOffset(tokens, cursor);
        int withIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBeforeAtDepth(tokens, cursor, depth,
                MySqlLexer.WITH);
        if (withIndex < 0) {
            return List.of();
        }
        List<MysqlSqlCompletionRelationScope.Relation> relations = new ArrayList<>();
        for (int index = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, withIndex + 1);
             index >= 0 && index < tokens.size(); ) {
            Token nameToken = tokens.get(index);
            if (nameToken.getStartIndex() >= cursor || !MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(nameToken)) {
                break;
            }
            int asIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndexBeforeAtDepth(tokens, index + 1, cursor, 0,
                    MySqlLexer.AS, MySqlLexer.SELECT);
            if (asIndex < 0 || tokens.get(asIndex).getType() != MySqlLexer.AS) {
                break;
            }
            int openIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, asIndex + 1);
            if (openIndex < 0 || tokens.get(openIndex).getType() != MySqlLexer.LR_BRACKET) {
                break;
            }
            int closeIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, openIndex);
            if (closeIndex < 0 || tokens.get(closeIndex).getStopIndex() >= cursor) {
                break;
            }
            String name = MysqlSqlCompletionTokenUtil.stripQuote(nameToken.getText());
            List<String> columns = selectProjectionColumns(tokens, openIndex + 1, closeIndex);
            relations.add(MysqlSqlCompletionRelationScope.Relation.local(name, null, columns));
            int nextIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, closeIndex + 1);
            if (nextIndex < 0 || nextIndex >= tokens.size()
                    || tokens.get(nextIndex).getType() != MySqlLexer.COMMA
                    || tokens.get(nextIndex).getStartIndex() >= cursor) {
                break;
            }
            index = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, nextIndex + 1);
        }
        return relations;
    }

    static List<MysqlSqlCompletionRelationScope.Relation> resolveDerivedTables(List<Token> tokens, int cursor) {
        List<MysqlSqlCompletionRelationScope.Relation> relations = new ArrayList<>();
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (token.getStartIndex() >= cursor) {
                break;
            }
            if (token.getType() != MySqlLexer.LR_BRACKET) {
                continue;
            }
            if (!isDerivedTableOpen(tokens, index)) {
                continue;
            }
            int closeIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, index);
            if (closeIndex < 0 || tokens.get(closeIndex).getStopIndex() >= cursor) {
                continue;
            }
            int selectIndex = derivedTableSelectIndex(tokens, index, closeIndex);
            if (selectIndex < 0) {
                continue;
            }
            Optional<String> alias = derivedAlias(tokens, closeIndex + 1, cursor);
            if (alias.isEmpty()) {
                continue;
            }
            List<String> columns = selectProjectionColumns(tokens, selectIndex, closeIndex);
            relations.add(MysqlSqlCompletionRelationScope.Relation.local(alias.get(), alias.get(), columns));
            index = closeIndex;
        }
        return relations;
    }

    private static boolean isDerivedTableOpen(List<Token> tokens, int openIndex) {
        int previousIndex = MysqlSqlCompletionTokenUtil.previousDefaultIndex(tokens, openIndex - 1);
        if (previousIndex < 0) {
            return false;
        }
        int tokenType = tokens.get(previousIndex).getType();
        return tokenType == MySqlLexer.FROM
                || tokenType == MySqlLexer.JOIN
                || tokenType == MySqlLexer.STRAIGHT_JOIN
                || tokenType == MySqlLexer.COMMA;
    }

    private static int derivedTableSelectIndex(List<Token> tokens, int openIndex, int closeIndex) {
        int headIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, openIndex + 1);
        if (headIndex < 0 || headIndex >= closeIndex) {
            return -1;
        }
        int headType = tokens.get(headIndex).getType();
        if (headType != MySqlLexer.SELECT && headType != MySqlLexer.WITH) {
            return -1;
        }
        int depth = 0;
        for (int index = headIndex; index < closeIndex; index++) {
            Token token = tokens.get(index);
            if (depth == 0 && token.getType() == MySqlLexer.SELECT) {
                return index;
            }
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
            }
        }
        return -1;
    }

    private static Optional<String> derivedAlias(List<Token> tokens, int start, int cursor) {
        int index = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, start);
        if (index < 0 || tokens.get(index).getStartIndex() >= cursor) {
            return Optional.empty();
        }
        if (tokens.get(index).getType() == MySqlLexer.AS) {
            index = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, index + 1);
            if (index < 0 || tokens.get(index).getStartIndex() >= cursor) {
                return Optional.empty();
            }
        }
        Token token = tokens.get(index);
        if (!MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(token)) {
            return Optional.empty();
        }
        return Optional.of(MysqlSqlCompletionTokenUtil.stripQuote(token.getText()));
    }

    private static List<String> selectProjectionColumns(List<Token> tokens, int start, int endExclusive) {
        int selectIndex = MysqlSqlCompletionTokenUtil.firstDefaultIndex(tokens, start, endExclusive, MySqlLexer.SELECT);
        if (selectIndex < 0) {
            return List.of();
        }
        int projectionEnd = projectionEnd(tokens, selectIndex + 1, endExclusive);
        LinkedHashSet<String> columns = new LinkedHashSet<>();
        int elementStart = selectIndex + 1;
        int depth = 0;
        for (int index = selectIndex + 1; index < projectionEnd; index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
                continue;
            }
            if (depth == 0 && token.getType() == MySqlLexer.COMMA) {
                projectionColumn(tokens, elementStart, index).ifPresent(columns::add);
                elementStart = index + 1;
            }
        }
        projectionColumn(tokens, elementStart, projectionEnd).ifPresent(columns::add);
        return List.copyOf(columns);
    }

    private static Optional<String> projectionColumn(List<Token> tokens, int start, int endExclusive) {
        int lastAsIndex = -1;
        int depth = 0;
        for (int index = Math.max(0, start); index < Math.min(endExclusive, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                depth = Math.max(0, depth - 1);
                continue;
            }
            if (depth == 0 && token.getType() == MySqlLexer.AS) {
                lastAsIndex = index;
            }
        }
        if (lastAsIndex >= 0) {
            int aliasIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, lastAsIndex + 1);
            if (aliasIndex >= 0 && aliasIndex < endExclusive
                    && MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(tokens.get(aliasIndex))) {
                return Optional.of(MysqlSqlCompletionTokenUtil.stripQuote(tokens.get(aliasIndex).getText()));
            }
        }
        int lastIdentifier = -1;
        for (int index = Math.max(0, start); index < Math.min(endExclusive, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (MysqlSqlCompletionTokenUtil.isUnqualifiedIdentifierToken(token)
                    || token.getType() == MySqlLexer.DOT_ID) {
                lastIdentifier = index;
            }
        }
        if (lastIdentifier < 0) {
            return Optional.empty();
        }
        return Optional.of(MysqlSqlCompletionTokenUtil.stripQuote(
                MysqlSqlCompletionTokenUtil.stripLeadingDot(tokens.get(lastIdentifier).getText())));
    }

    private static int projectionEnd(List<Token> tokens, int start, int endExclusive) {
        int depth = 0;
        for (int index = Math.max(0, start); index < Math.min(endExclusive, tokens.size()); index++) {
            Token token = tokens.get(index);
            if (token.getType() == MySqlLexer.LR_BRACKET) {
                depth++;
                continue;
            }
            if (token.getType() == MySqlLexer.RR_BRACKET) {
                if (depth == 0) {
                    return index;
                }
                depth--;
                continue;
            }
            if (depth == 0 && SELECT_BOUNDARY_TOKENS.contains(token.getType())) {
                return index;
            }
        }
        return endExclusive;
    }

}
