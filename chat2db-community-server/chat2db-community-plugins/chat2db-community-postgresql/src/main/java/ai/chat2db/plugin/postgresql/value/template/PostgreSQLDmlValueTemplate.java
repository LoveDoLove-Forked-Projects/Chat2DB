package ai.chat2db.plugin.postgresql.value.template;

import static ai.chat2db.plugin.postgresql.constant.PostgreSQLDmlValueTemplateConstants.*;



public class PostgreSQLDmlValueTemplate {






    public static String wrapBit(String value) {
        return String.format(BIT_TEMPLATE, value);
    }
    public static String wrapBytea(String value) {
        return String.format(BYTEA_VALUE, value);
    }

    public static String wrapJsonb(String value) {
        return String.format(JSONB_TEMPLATE, value);
    }

    public static String wrapJson(String value) {
        return String.format(JSON_TEMPLATE, value);
    }
}
