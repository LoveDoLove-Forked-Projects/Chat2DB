package ai.chat2db.plugin.oracle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OracleMetaDataTest {

    @Test
    void appendRoutineSourceTextPreservesOracleLineTerminators() {
        StringBuilder builder = new StringBuilder("CREATE OR REPLACE ");

        OracleMetaData.appendRoutineSourceText(builder, "procedure p_test(\n");
        OracleMetaData.appendRoutineSourceText(builder, "  p_id in number\n");
        OracleMetaData.appendRoutineSourceText(builder, ")\n");

        assertEquals("CREATE OR REPLACE procedure p_test(\n  p_id in number\n)\n", builder.toString());
    }

    @Test
    void appendRoutineSourceTextAddsSeparatorWhenMissing() {
        StringBuilder builder = new StringBuilder("CREATE OR REPLACE ");

        OracleMetaData.appendRoutineSourceText(builder, "procedure p_test(");
        OracleMetaData.appendRoutineSourceText(builder, "  p_id in number");
        OracleMetaData.appendRoutineSourceText(builder, ")");

        assertEquals("CREATE OR REPLACE procedure p_test(\n  p_id in number\n)\n", builder.toString());
    }

    @Test
    void appendRoutineSourceTextPreservesExplicitBlankLines() {
        StringBuilder builder = new StringBuilder();

        OracleMetaData.appendRoutineSourceText(builder, "  update products\n");
        OracleMetaData.appendRoutineSourceText(builder, "\n");
        OracleMetaData.appendRoutineSourceText(builder, "  if sql%rowcount = 0 then\n");

        assertEquals("  update products\n\n  if sql%rowcount = 0 then\n", builder.toString());
    }
}
