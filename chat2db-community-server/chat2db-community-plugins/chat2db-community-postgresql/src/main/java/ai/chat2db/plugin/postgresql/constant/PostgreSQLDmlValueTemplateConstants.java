package ai.chat2db.plugin.postgresql.constant;

public final class PostgreSQLDmlValueTemplateConstants {

    public static final String BYTEA_VALUE = "E'\\\\x%s'::bytea";
    public static final String JSON_TEMPLATE = "'%s'::json";
    public static final String JSONB_TEMPLATE = "'%s'::jsonb";
    public static final String BIT_TEMPLATE = "B'%s'";

    private PostgreSQLDmlValueTemplateConstants() {
    }
}
