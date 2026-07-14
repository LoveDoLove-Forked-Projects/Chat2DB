package ai.chat2db.plugin.mysql.value.factory;

import ai.chat2db.plugin.mysql.enums.type.MysqlColumnTypeEnum;
import ai.chat2db.plugin.mysql.value.sub.*;
import ai.chat2db.spi.DefaultValueProcessor;

import java.util.Map;


public class MysqlValueProcessorFactory {

    private static final Map<String, DefaultValueProcessor> PROCESSOR_MAP;

    static {
        MysqlGeometryProcessor mysqlGeometryProcessor = new MysqlGeometryProcessor();
        MysqlVarBinaryProcessor mysqlVarBinaryProcessor = new MysqlVarBinaryProcessor();
        MysqlTimestampProcessor mysqlTimestampProcessor = new MysqlTimestampProcessor();
        MysqlTextProcessor mysqlTextProcessor = new MysqlTextProcessor();
        PROCESSOR_MAP = Map.<String, DefaultValueProcessor>ofEntries(
                Map.entry(MysqlColumnTypeEnum.TEXT.name(), mysqlTextProcessor),
                Map.entry(MysqlColumnTypeEnum.TINYTEXT.name(), mysqlTextProcessor),
                Map.entry(MysqlColumnTypeEnum.MEDIUMTEXT.name(), mysqlTextProcessor),
                Map.entry(MysqlColumnTypeEnum.LONGTEXT.name(), mysqlTextProcessor),
                Map.entry(MysqlColumnTypeEnum.GEOMETRY.name(), mysqlGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.POINT.name(), mysqlGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.LINESTRING.name(), mysqlGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.POLYGON.name(), mysqlGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.MULTIPOINT.name(), mysqlGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.MULTILINESTRING.name(), mysqlGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.MULTIPOLYGON.name(), mysqlGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.GEOMETRYCOLLECTION.name(), mysqlGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.VARBINARY.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.BLOB.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.LONGBLOB.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.TINYBLOB.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.MEDIUMBLOB.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.TIMESTAMP.name(), mysqlTimestampProcessor),
                Map.entry(MysqlColumnTypeEnum.DATETIME.name(), mysqlTimestampProcessor),
                Map.entry(MysqlColumnTypeEnum.YEAR.name(), new MysqlYearProcessor()),
                Map.entry(MysqlColumnTypeEnum.BIT.name(), new MysqlBitProcessor()),
                Map.entry(MysqlColumnTypeEnum.DECIMAL.name(), new MysqlDecimalProcessor()),
                Map.entry(MysqlColumnTypeEnum.BINARY.name(), new MysqlBinaryProcessor()),
                Map.entry(MysqlColumnTypeEnum.TINYINT.name(), new MysqlTinyintProcessor()),
                Map.entry(MysqlColumnTypeEnum.FLOAT.name(), new MysqlFloatProcessor()),
                Map.entry(MysqlColumnTypeEnum.DOUBLE.name(), new MysqlDoubleProcessor())

        );
    }

    public static DefaultValueProcessor getValueProcessor(String type) {
        return PROCESSOR_MAP.get(type);
    }
}
