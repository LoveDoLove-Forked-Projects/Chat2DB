package ai.chat2db.plugin.mysql.model.completion.context;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.completion.slot.MysqlSqlCompletionRuleSlot;
import ai.chat2db.community.domain.api.service.db.ISqlCompletionMetadataProvider;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalContext;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionInputCleanResponse;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionDummySql;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;


public record MysqlSqlCompletionCandidateContext(DbSqlCompletionRequest request,
                                                 SqlCompletionInputCleanResponse input,
                                                 SqlCompletionStatementWindow window,
                                                 SqlCompletionDummySql dummySql,
                                                 SqlCompletionCursorContext cursorContext,
                                                 SqlCompletionLocalContext localContext,
                                                 MysqlSqlCompletionInsertStatementContext insertStatementContext,
                                                 SqlCompletionCandidates c3Result,
                                                 MysqlSqlCompletionRuleSlot ruleSlot) {

    public MysqlSqlCompletionCandidateContext(DbSqlCompletionRequest request,
                                              SqlCompletionInputCleanResponse input,
                                              SqlCompletionStatementWindow window,
                                              SqlCompletionDummySql dummySql,
                                              SqlCompletionCursorContext cursorContext) {
        this(request, input, window, dummySql, cursorContext, SqlCompletionLocalContext.empty(),
                MysqlSqlCompletionInsertStatementContext.inactive(), SqlCompletionCandidates.unavailable(),
                MysqlSqlCompletionRuleSlot.unknown());
    }

    public MysqlSqlCompletionCandidateContext(DbSqlCompletionRequest request,
                                              SqlCompletionInputCleanResponse input,
                                              SqlCompletionStatementWindow window,
                                              SqlCompletionDummySql dummySql,
                                              SqlCompletionCursorContext cursorContext,
                                              MysqlSqlCompletionInsertStatementContext insertStatementContext) {
        this(request, input, window, dummySql, cursorContext, SqlCompletionLocalContext.empty(), insertStatementContext,
                SqlCompletionCandidates.unavailable(), MysqlSqlCompletionRuleSlot.unknown());
    }

    public MysqlSqlCompletionCandidateContext(DbSqlCompletionRequest request,
                                              SqlCompletionInputCleanResponse input,
                                              SqlCompletionStatementWindow window,
                                              SqlCompletionDummySql dummySql,
                                              SqlCompletionCursorContext cursorContext,
                                              SqlCompletionLocalContext localContext,
                                              MysqlSqlCompletionInsertStatementContext insertStatementContext) {
        this(request, input, window, dummySql, cursorContext, localContext, insertStatementContext,
                SqlCompletionCandidates.unavailable(), MysqlSqlCompletionRuleSlot.unknown());
    }

    public MysqlSqlCompletionCandidateContext {
        localContext = localContext == null ? SqlCompletionLocalContext.empty() : localContext;
        insertStatementContext = insertStatementContext == null
                ? MysqlSqlCompletionInsertStatementContext.inactive()
                : insertStatementContext;
        c3Result = c3Result == null ? SqlCompletionCandidates.unavailable() : c3Result;
        ruleSlot = ruleSlot == null ? MysqlSqlCompletionRuleSlot.unknown() : ruleSlot;
    }

    public ISqlCompletionMetadataProvider metadataProvider() {
        return request.metadataProvider();
    }

    public String prefix() {
        return cursorContext.prefix();
    }

    public int replaceStart() {
        return window.sourceStartOffset() + cursorContext.replaceStart();
    }

    public int replaceEnd() {
        return window.sourceStartOffset() + cursorContext.replaceEnd();
    }
}
