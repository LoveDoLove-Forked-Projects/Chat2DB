package ai.chat2db.plugin.mysql.constant;

public final class MysqlDmlValueTemplateConstants {

    public static final String GEOMETRY_TEMPLATE = "ST_GeomFromText('%s')";
    public static final String BIT_TEMPLATE = "b'%s'";
    public static final String HEX_TEMPLATE = "0x%s";

    private MysqlDmlValueTemplateConstants() {
    }
}
