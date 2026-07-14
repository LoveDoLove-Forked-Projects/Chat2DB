package ai.chat2db.spi.parser.completion;

import ai.chat2db.spi.ISqlCompletionCollector;
import ai.chat2db.spi.ISqlCompletionLocalContextCollector;
import ai.chat2db.spi.ISqlCompletionDummyBuilder;
import ai.chat2db.spi.ISqlCompletionRuleEvidenceCollector;
import ai.chat2db.spi.ISqlCompletionInputCleaner;
import ai.chat2db.spi.ISqlCompletionIntentResolver;
import ai.chat2db.spi.ISqlCompletionCandidatePlanner;
import ai.chat2db.spi.ISqlCompletionPresentationProcessor;
import ai.chat2db.spi.ISqlCompletionSlotClassifier;
import ai.chat2db.spi.ISqlCompletionCursorAnalyzer;
import ai.chat2db.spi.ISqlCompletionStatementLocator;
import java.util.Objects;


public record SqlCompletionDialectComponents(ISqlCompletionInputCleaner inputCleaner,
                                             ISqlCompletionStatementLocator statementLocator,
                                             ISqlCompletionCursorAnalyzer cursorAnalyzer,
                                             ISqlCompletionDummyBuilder dummyBuilder,
                                             ISqlCompletionLocalContextCollector localContextCollector,
                                             ISqlCompletionCollector c3Collector,
                                             ISqlCompletionRuleEvidenceCollector ruleEvidenceCollector,
                                             ISqlCompletionSlotClassifier slotClassifier,
                                             ISqlCompletionIntentResolver intentResolver,
                                             ISqlCompletionCandidatePlanner candidatePlanner,
                                             ISqlCompletionPresentationProcessor presentationProcessor) {

    public SqlCompletionDialectComponents {
        Objects.requireNonNull(inputCleaner, "inputCleaner");
        Objects.requireNonNull(statementLocator, "statementLocator");
        Objects.requireNonNull(cursorAnalyzer, "cursorAnalyzer");
        Objects.requireNonNull(dummyBuilder, "dummyBuilder");
        Objects.requireNonNull(localContextCollector, "localContextCollector");
        Objects.requireNonNull(c3Collector, "c3Collector");
        Objects.requireNonNull(ruleEvidenceCollector, "ruleEvidenceCollector");
        Objects.requireNonNull(slotClassifier, "slotClassifier");
        Objects.requireNonNull(intentResolver, "intentResolver");
        Objects.requireNonNull(candidatePlanner, "candidatePlanner");
        Objects.requireNonNull(presentationProcessor, "presentationProcessor");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ISqlCompletionInputCleaner inputCleaner;
        private ISqlCompletionStatementLocator statementLocator;
        private ISqlCompletionCursorAnalyzer cursorAnalyzer;
        private ISqlCompletionDummyBuilder dummyBuilder;
        private ISqlCompletionLocalContextCollector localContextCollector;
        private ISqlCompletionCollector c3Collector;
        private ISqlCompletionRuleEvidenceCollector ruleEvidenceCollector;
        private ISqlCompletionSlotClassifier slotClassifier;
        private ISqlCompletionIntentResolver intentResolver;
        private ISqlCompletionCandidatePlanner candidatePlanner;
        private ISqlCompletionPresentationProcessor presentationProcessor;

        public Builder inputCleaner(ISqlCompletionInputCleaner inputCleaner) {
            this.inputCleaner = inputCleaner;
            return this;
        }

        public Builder statementLocator(ISqlCompletionStatementLocator statementLocator) {
            this.statementLocator = statementLocator;
            return this;
        }

        public Builder cursorAnalyzer(ISqlCompletionCursorAnalyzer cursorAnalyzer) {
            this.cursorAnalyzer = cursorAnalyzer;
            return this;
        }

        public Builder dummyBuilder(ISqlCompletionDummyBuilder dummyBuilder) {
            this.dummyBuilder = dummyBuilder;
            return this;
        }

        public Builder localContextCollector(ISqlCompletionLocalContextCollector localContextCollector) {
            this.localContextCollector = localContextCollector;
            return this;
        }

        public Builder c3Collector(ISqlCompletionCollector c3Collector) {
            this.c3Collector = c3Collector;
            return this;
        }

        public Builder ruleEvidenceCollector(ISqlCompletionRuleEvidenceCollector ruleEvidenceCollector) {
            this.ruleEvidenceCollector = ruleEvidenceCollector;
            return this;
        }

        public Builder slotClassifier(ISqlCompletionSlotClassifier slotClassifier) {
            this.slotClassifier = slotClassifier;
            return this;
        }

        public Builder intentResolver(ISqlCompletionIntentResolver intentResolver) {
            this.intentResolver = intentResolver;
            return this;
        }

        public Builder candidatePlanner(ISqlCompletionCandidatePlanner candidatePlanner) {
            this.candidatePlanner = candidatePlanner;
            return this;
        }

        public Builder presentationProcessor(ISqlCompletionPresentationProcessor presentationProcessor) {
            this.presentationProcessor = presentationProcessor;
            return this;
        }

        public SqlCompletionDialectComponents build() {
            return new SqlCompletionDialectComponents(inputCleaner, statementLocator, cursorAnalyzer, dummyBuilder,
                    localContextCollector, c3Collector, ruleEvidenceCollector, slotClassifier, intentResolver,
                    candidatePlanner,
                    presentationProcessor);
        }
    }
}
