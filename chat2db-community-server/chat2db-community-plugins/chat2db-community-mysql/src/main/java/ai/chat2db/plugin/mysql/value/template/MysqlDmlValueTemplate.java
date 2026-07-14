package ai.chat2db.plugin.mysql.value.template;

import static ai.chat2db.plugin.mysql.constant.MysqlDmlValueTemplateConstants.*;



public class MysqlDmlValueTemplate {





    public static String wrapGeometry(String value) {
        return String.format(GEOMETRY_TEMPLATE, value);
    }

    public static String wrapBit(String value) {
        return String.format(BIT_TEMPLATE, value);
    }

    public static String wrapHex(String value) {
        return String.format(HEX_TEMPLATE, value);
    }
}
