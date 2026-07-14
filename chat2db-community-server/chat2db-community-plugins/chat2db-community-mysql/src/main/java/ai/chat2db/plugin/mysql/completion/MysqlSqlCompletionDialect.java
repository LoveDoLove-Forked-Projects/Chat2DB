package ai.chat2db.plugin.mysql.completion;

import ai.chat2db.plugin.mysql.completion.analysis.MysqlSqlCompletionCursorAnalyzer;
import ai.chat2db.plugin.mysql.completion.c3.MysqlSqlCompletionEngine;
import ai.chat2db.plugin.mysql.completion.context.MysqlSqlCompletionCandidateContextFactory;
import ai.chat2db.plugin.mysql.completion.context.MysqlSqlCompletionLocalContextCollector;
import ai.chat2db.plugin.mysql.completion.dummy.MysqlSqlCompletionDummyBuilder;
import ai.chat2db.plugin.mysql.completion.evidence.MysqlSqlCompletionRuleEvidenceCollector;
import ai.chat2db.plugin.mysql.completion.hint.MysqlSqlCompletionEditorHintBuilder;
import ai.chat2db.plugin.mysql.completion.intent.MysqlSqlCompletionIntentResolver;
import ai.chat2db.plugin.mysql.completion.locate.MysqlSqlCompletionStatementLocator;
import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidatePlanner;
import ai.chat2db.plugin.mysql.completion.presentation.MysqlSqlCompletionPresentationProcessor;
import ai.chat2db.plugin.mysql.completion.slot.MysqlSqlCompletionSlotClassifier;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionInputCleaner;
import ai.chat2db.spi.ISqlCompletionDialect;
import ai.chat2db.spi.parser.completion.SqlCompletionDialectComponents;


public final class MysqlSqlCompletionDialect implements ISqlCompletionDialect {

    private final MysqlSqlCompletionCandidateContextFactory contextFactory =
            new MysqlSqlCompletionCandidateContextFactory();

    @Override
    public SqlCompletionDialectComponents components() {
        return SqlCompletionDialectComponents.builder()
                .inputCleaner(state -> MysqlSqlCompletionInputCleaner.clean(state.request().sql(),
                        state.request().cursor()))
                .statementLocator(new MysqlSqlCompletionStatementLocator())
                .cursorAnalyzer(new MysqlSqlCompletionCursorAnalyzer())
                .dummyBuilder(new MysqlSqlCompletionDummyBuilder())
                .localContextCollector(new MysqlSqlCompletionLocalContextCollector())
                .c3Collector(state -> MysqlSqlCompletionEngine.collect(contextFactory.create(state)))
                .ruleEvidenceCollector(new MysqlSqlCompletionRuleEvidenceCollector())
                .slotClassifier(new MysqlSqlCompletionSlotClassifier(contextFactory))
                .intentResolver(new MysqlSqlCompletionIntentResolver())
                .candidatePlanner(new MysqlSqlCompletionCandidatePlanner())
                .presentationProcessor(new MysqlSqlCompletionPresentationProcessor(
                        new MysqlSqlCompletionEditorHintBuilder(), contextFactory))
                .build();
    }
}
