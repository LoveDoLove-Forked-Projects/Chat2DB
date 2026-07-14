package ai.chat2db.plugin.sqlserver.value.template;

import static ai.chat2db.plugin.sqlserver.constant.SqlServerDmlValueTemplateConstants.*;



public class SqlServerDmlValueTemplate {




    public static String wrapBinary(String value) {
        return String.format(BINARY_TEMPLATE, value);
    }

    public static String wrapGeography(String value) {
        return String.format(GEOGRAPHY_TEMPLATE, value);
    }

    public static String wrapString(String value) {
        return String.format(STRING_TEMPLATE, value);
    }
}
