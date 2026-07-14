package ai.chat2db.spi;

import ai.chat2db.spi.parser.completion.SqlCompletionDialectComponents;


public interface ISqlCompletionDialect {

    /**
     * Returns the component set used to assemble the fixed completion pipeline.
     *
     * @return dialect-specific completion components.
     */
    SqlCompletionDialectComponents components();
}
