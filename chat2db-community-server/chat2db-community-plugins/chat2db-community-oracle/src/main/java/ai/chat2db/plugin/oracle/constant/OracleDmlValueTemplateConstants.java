package ai.chat2db.plugin.oracle.constant;

public final class OracleDmlValueTemplateConstants {

    public static final String DATE_TEMPLATE = "TO_DATE('%s', 'SYYYY-MM-DD HH24:MI:SS')";
    public static final String TIMESTAMP_TEMPLATE = "TO_TIMESTAMP('%s', 'SYYYY-MM-DD HH24:MI:SS.FF%d')";
    public static final String TIMESTAMP_TZ_TEMPLATE = "TO_TIMESTAMP_TZ('%s', 'SYYYY-MM-DD HH24:MI:SS.FF%d TZR')";
    public static final String TIMESTAMP_TZ_WITHOUT_NANOS_TEMPLATE = "TO_TIMESTAMP_TZ('%s', 'SYYYY-MM-DD HH24:MI:SS TZR')";
    public static final String INTERVAL_YEAR_TO_MONTH_TEMPLATE = "INTERVAL '%s' YEAR(%d) TO MONTH";
    public static final String INTERVAL_DAY_TO_SECOND_TEMPLATE = "INTERVAL '%s' DAY(%d) TO SECOND(%d)";
    public static final String XML_TEMPLATE = "XMLType('%s')";

    private OracleDmlValueTemplateConstants() {
    }
}
