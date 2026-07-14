package ai.chat2db.plugin.mysql.completion.provider.column;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildPlan;
import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionContextGuards;
import ai.chat2db.plugin.mysql.config.completion.MysqlSqlCompletionFunctionTokenConfig;
import ai.chat2db.plugin.mysql.completion.context.MysqlSqlCompletionLocalContextRelations;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.plugin.mysql.completion.provider.object.MysqlSqlCompletionObjectCandidateProvider;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionColumnReferenceCandidateProvider {

    private MysqlSqlCompletionColumnReferenceCandidateProvider() {
    }

    public static MysqlSqlCompletionCandidateBuildPlan build(MysqlSqlCompletionCandidateContext context) {
        Optional<MysqlSqlCompletionCandidateBuildResult> localColumns = localColumns(context);
        if (localColumns.isPresent()) {
            return MysqlSqlCompletionCandidateBuildPlan.primary(localColumns.get());
        }
        MysqlSqlCompletionRelationScope relationScope = relationScope(context);
        return buildWithRelationScope(context, relationScope);
    }

    private static Optional<MysqlSqlCompletionCandidateBuildResult> localColumns(
            MysqlSqlCompletionCandidateContext context) {
        if (referencedColumnListOwner(context).isPresent()) {
            return Optional.empty();
        }
        return MysqlSqlCompletionLocalColumnCandidateProvider.build(context);
    }

    private static Optional<String> referencedColumnListOwner(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return Optional.empty();
        }
        List<Token> tokens = MysqlSqlCompletionTokenUtil.defaultTokens(context.window().parseSql());
        int cursor = Math.max(0, Math.min(context.cursorContext().replaceStart(), context.window().parseSql().length()));
        int referencesIndex = MysqlSqlCompletionTokenUtil.lastDefaultIndexBefore(tokens, cursor, MySqlLexer.REFERENCES);
        if (referencesIndex < 0) {
            return Optional.empty();
        }
        int tableIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, referencesIndex + 1);
        if (tableIndex < 0) {
            return Optional.empty();
        }
        int tableEndIndex = MysqlSqlCompletionTokenUtil.qualifiedIdentifierEndIndex(tokens, tableIndex);
        if (tableEndIndex < tableIndex || !MysqlSqlCompletionTokenUtil.tokenEndsAtOrBefore(tokens.get(tableEndIndex),
                cursor)) {
            return Optional.empty();
        }
        int openIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, tableEndIndex + 1);
        if (openIndex < 0 || tokens.get(openIndex).getType() != MySqlLexer.LR_BRACKET) {
            return Optional.empty();
        }
        int closeIndex = MysqlSqlCompletionTokenUtil.matchingRightBracket(tokens, openIndex);
        boolean insideColumnList = closeIndex < 0
                ? tokens.get(openIndex).getStartIndex() < cursor
                : MysqlSqlCompletionTokenUtil.containsCursor(tokens, openIndex, closeIndex, cursor);
        if (!insideColumnList) {
            return Optional.empty();
        }
        return Optional.of(referencedOwner(tokens, tableEndIndex))
                .filter(StringUtils::isNotBlank);
    }

    private static MysqlSqlCompletionCandidateBuildPlan buildWithRelationScope(
            MysqlSqlCompletionCandidateContext context,
            MysqlSqlCompletionRelationScope relationScope) {
        MysqlSqlCompletionCandidateBuildResult aliases =
                StringUtils.isBlank(context.prefix())
                        ? MysqlSqlCompletionCandidateBuildResult.empty()
                        : MysqlSqlCompletionRelationAliasCandidateProvider.build(context, relationScope);
        if (!aliases.candidates().isEmpty()) {
            return MysqlSqlCompletionCandidateBuildPlan.primary(aliases);
        }
        if (MysqlSqlCompletionContextGuards.invalidFixedAggregateExtraArgument(context)) {
            return MysqlSqlCompletionCandidateBuildPlan.empty();
        }
        MysqlSqlCompletionCandidateBuildResult metadataFunctions = includeMetadataFunctions(context, relationScope)
                ? MysqlSqlCompletionObjectCandidateProvider.buildCallable(context, SqlCompletionCandidateTypeEnum.FUNCTION)
                : MysqlSqlCompletionCandidateBuildResult.empty();
        MysqlSqlCompletionCandidateBuildResult columns =
                MysqlSqlCompletionColumnCandidateProvider.build(context, relationScope);
        return new MysqlSqlCompletionCandidateBuildPlan(columns, metadataFunctions);
    }

    public static MysqlSqlCompletionCandidateBuildResult buildBlankPrefixRelationAliases(
            MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.cursorContext() == null || context.cursorContext().dotScoped()
                || StringUtils.isNotBlank(context.prefix())) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        MysqlSqlCompletionRelationScope relationScope = relationScope(context);
        return MysqlSqlCompletionRelationAliasCandidateProvider.build(context, relationScope);
    }

    public static MysqlSqlCompletionCandidateBuildPlan buildInsertValueExpression(
            MysqlSqlCompletionCandidateContext context) {
        MysqlSqlCompletionCandidateBuildResult metadataFunctions = MysqlSqlCompletionObjectCandidateProvider
                .buildCallable(context, SqlCompletionCandidateTypeEnum.FUNCTION);
        return new MysqlSqlCompletionCandidateBuildPlan(MysqlSqlCompletionCandidateBuildResult.empty(),
                metadataFunctions);
    }

    private static boolean includeMetadataFunctions(MysqlSqlCompletionCandidateContext context,
                                                    MysqlSqlCompletionRelationScope relationScope) {
        if (!MysqlSqlCompletionFunctionTokenConfig.hasCurrentFunctionRule(context.c3Result())) {
            return false;
        }
        if (!context.cursorContext().dotScoped()) {
            return true;
        }
        if (relationScope.relations().isEmpty()) {
            return false;
        }
        String owner = context.cursorContext().scope().table();
        return relationScope.resolveOwner(owner).isEmpty();
    }

    private static MysqlSqlCompletionRelationScope relationScope(MysqlSqlCompletionCandidateContext context) {
        Optional<String> referencedOwner = referencedColumnListOwner(context);
        return referencedOwner.map(s -> new MysqlSqlCompletionRelationScope(
                MysqlSqlCompletionLocalContextRelations.columnRelations(context).stream()
                        .filter(relation -> relation != null && relation.matches(s))
                        .toList())).orElseGet(() -> new MysqlSqlCompletionRelationScope(MysqlSqlCompletionLocalContextRelations.columnRelations(context)));
    }

    private static String referencedOwner(List<Token> tokens, int tableEndIndex) {
        return MysqlSqlCompletionTokenUtil.identifierText(tokens.get(tableEndIndex));
    }
}
