package ai.chat2db.plugin.sqlserver.value.factory;

import ai.chat2db.plugin.sqlserver.enums.type.SqlServerColumnTypeEnum;
import ai.chat2db.plugin.sqlserver.value.sub.*;
import ai.chat2db.spi.DefaultValueProcessor;

import java.util.Map;


public class SqlServerValueProcessorFactory {

    private static final Map<String, DefaultValueProcessor> PROCESSOR_MAP;

    static {
        SqlServerBinaryProcessor sqlServerBinaryProcessor = new SqlServerBinaryProcessor();
        SqlServerTextProcessor sqlServerTextProcessor = new SqlServerTextProcessor();
        SqlServerStringProcessor sqlServerStringProcessor = new SqlServerStringProcessor();
        SqlServerDataTime2Processor sqlServerDataTime2Processor = new SqlServerDataTime2Processor();
        PROCESSOR_MAP = Map.ofEntries(
                Map.entry(SqlServerColumnTypeEnum.BINARY.name(), sqlServerBinaryProcessor),
                Map.entry(SqlServerColumnTypeEnum.VARBINARY.name(), sqlServerBinaryProcessor),
                Map.entry(SqlServerColumnTypeEnum.IMAGE.name(), new SqlServerImageProcessor()),
                Map.entry(SqlServerColumnTypeEnum.TEXT.name(), sqlServerTextProcessor),
                Map.entry(SqlServerColumnTypeEnum.NTEXT.name(), sqlServerTextProcessor),
                Map.entry(SqlServerColumnTypeEnum.GEOGRAPHY.name(), new SqlServerGeographyProcessor()),
                Map.entry(SqlServerColumnTypeEnum.XML.name(), new SqlServerXmlProcessor()),
                Map.entry(SqlServerColumnTypeEnum.CHAR.name(), sqlServerStringProcessor),
                Map.entry(SqlServerColumnTypeEnum.VARCHAR.name(), sqlServerStringProcessor),
                Map.entry(SqlServerColumnTypeEnum.NVARCHAR.name(), sqlServerStringProcessor),
                Map.entry(SqlServerColumnTypeEnum.NCHAR.name(), sqlServerStringProcessor),
                Map.entry(SqlServerColumnTypeEnum.BIT.name(), new SqlServerBitProcessor()),
                Map.entry(SqlServerColumnTypeEnum.DATETIME.name(), sqlServerDataTime2Processor),
                Map.entry(SqlServerColumnTypeEnum.DATETIME2.name(), sqlServerDataTime2Processor),
                Map.entry(SqlServerColumnTypeEnum.TIME.name(), sqlServerDataTime2Processor),
                Map.entry(SqlServerColumnTypeEnum.DATETIMEOFFSET.name(), sqlServerDataTime2Processor)
        );
    }

    public static DefaultValueProcessor getValueProcessor(String type) {
        return PROCESSOR_MAP.get(type);
    }
}
