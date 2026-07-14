package ai.chat2db.plugin.oracle.value.factory;

import ai.chat2db.plugin.oracle.enums.type.OracleColumnTypeEnum;
import ai.chat2db.plugin.oracle.value.sub.*;
import ai.chat2db.spi.DefaultValueProcessor;

import java.util.Map;
public class OracleValueProcessorFactory {

    private static final Map<String, DefaultValueProcessor> PROCESSOR_MAP;

    static {
        OracleClobProcessor oracleClobProcessor = new OracleClobProcessor();
        OracleTimeStampProcessor oracleTimeStampProcessor = new OracleTimeStampProcessor();
        OracleBlobProcessor oracleBlobProcessor = new OracleBlobProcessor();
        OracleRawValueProcessor oracleRawValueProcessor = new OracleRawValueProcessor();
        PROCESSOR_MAP = Map.ofEntries(
                Map.entry(OracleColumnTypeEnum.CLOB.name(), oracleClobProcessor),
                Map.entry(OracleColumnTypeEnum.NCLOB.name(), oracleClobProcessor),
                Map.entry(OracleColumnTypeEnum.LONG.name(), new OracleLongProcessor()),
                Map.entry(OracleColumnTypeEnum.DATE.name(), new OracleDateProcessor()),
                Map.entry(OracleColumnTypeEnum.TIMESTAMP.name(), oracleTimeStampProcessor),
                Map.entry(OracleColumnTypeEnum.TIMESTAMP_WITH_LOCAL_TIME_ZONE.getColumnType().getTypeName(), new OracleTimeStampLTZProcessor()),
                Map.entry(OracleColumnTypeEnum.TIMESTAMP_WITH_TIME_ZONE.getColumnType().getTypeName(), new OracleTimeStampTZProcessor()),
                Map.entry("INTERVALDS", new OracleIntervalDSProcessor()),
                Map.entry("INTERVALYM", new OracleIntervalYMProcessor()),
                Map.entry(OracleColumnTypeEnum.NUMBER.name(), new OracleNumberProcessor()),
                Map.entry(OracleColumnTypeEnum.BLOB.name(), oracleBlobProcessor),
                Map.entry(OracleColumnTypeEnum.RAW.name(), oracleRawValueProcessor),
                Map.entry(OracleColumnTypeEnum.LONG_RAW.getColumnType().getTypeName(), new OracleLongRawProcessor()),
                Map.entry("SYS.XMLTYPE", new OracleXmlValueProcessor()),
                Map.entry("SYS.ANYDATA", new OracleAnyDataProcessor())
        );

    }

    public static DefaultValueProcessor getValueProcessor(String type) {
        return PROCESSOR_MAP.get(type);
    }

}
