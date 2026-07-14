package ai.chat2db.plugin.sqlserver.constant;

public final class SqlServerDmlValueTemplateConstants {

    public static final String BINARY_TEMPLATE = "0x%s";
    public static final String GEOGRAPHY_TEMPLATE = "geography::STGeomFromText('%s', 4326)";
    public static final String STRING_TEMPLATE = "N'%s'";

    private SqlServerDmlValueTemplateConstants() {
    }
}
