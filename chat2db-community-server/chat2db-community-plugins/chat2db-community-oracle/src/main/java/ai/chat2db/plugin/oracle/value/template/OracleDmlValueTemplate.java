package ai.chat2db.plugin.oracle.value.template;

import static ai.chat2db.plugin.oracle.constant.OracleDmlValueTemplateConstants.*;



public class OracleDmlValueTemplate {









    public static String wrapDate(String date) {
        return String.format(DATE_TEMPLATE, date);
    }

    public static String wrapTimestamp(String timestamp, int scale) {
        return String.format(TIMESTAMP_TEMPLATE, timestamp, scale);
    }

    public static String wrapTimestampTz(String timestamp, int scale) {
        return String.format(TIMESTAMP_TZ_TEMPLATE, timestamp, scale);
    }

    public static String wrapTimestampTzWithOutNanos(String timestamp) {
        return String.format(TIMESTAMP_TZ_WITHOUT_NANOS_TEMPLATE, timestamp);
    }

    public static String wrapIntervalYearToMonth(String year, int precision) {
        return String.format(INTERVAL_YEAR_TO_MONTH_TEMPLATE, year, precision);
    }

    public static String wrapIntervalDayToSecond(String day, int precision, int scale) {
        return String.format(INTERVAL_DAY_TO_SECOND_TEMPLATE, day, precision, scale);
    }

    public static String wrapXml(String xml) {
        return String.format(XML_TEMPLATE, xml);
    }

}
