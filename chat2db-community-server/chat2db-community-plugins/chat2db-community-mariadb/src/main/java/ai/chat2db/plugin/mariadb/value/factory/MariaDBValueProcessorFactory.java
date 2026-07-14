package ai.chat2db.plugin.mariadb.value.factory;

import ai.chat2db.plugin.mariadb.value.sub.MariaDBBitProcessor;
import ai.chat2db.plugin.mariadb.value.sub.MariaDBGeometryProcessor;
import ai.chat2db.plugin.mariadb.value.sub.MariaDBTimestampProcessor;
import ai.chat2db.plugin.mariadb.value.sub.MariaDBYearProcessor;
import ai.chat2db.plugin.mysql.enums.type.MysqlColumnTypeEnum;
import ai.chat2db.plugin.mysql.value.sub.MysqlBinaryProcessor;
import ai.chat2db.plugin.mysql.value.sub.MysqlDecimalProcessor;
import ai.chat2db.plugin.mysql.value.sub.MysqlTextProcessor;
import ai.chat2db.plugin.mysql.value.sub.MysqlVarBinaryProcessor;
import ai.chat2db.spi.DefaultValueProcessor;

import java.util.Map;


public class MariaDBValueProcessorFactory {

    private static final Map<String, DefaultValueProcessor> PROCESSOR_MAP;

    static {
        MariaDBGeometryProcessor mariaDBGeometryProcessor = new MariaDBGeometryProcessor();
        MysqlVarBinaryProcessor mysqlVarBinaryProcessor = new MysqlVarBinaryProcessor();
        MariaDBTimestampProcessor mariaDBTimestampProcessor = new MariaDBTimestampProcessor();
        MysqlTextProcessor mysqlTextProcessor = new MysqlTextProcessor();
        PROCESSOR_MAP = Map.ofEntries(
                Map.entry(MysqlColumnTypeEnum.TEXT.name(), mysqlTextProcessor),
                Map.entry(MysqlColumnTypeEnum.TINYTEXT.name(), mysqlTextProcessor),
                Map.entry(MysqlColumnTypeEnum.MEDIUMTEXT.name(), mysqlTextProcessor),
                Map.entry(MysqlColumnTypeEnum.LONGTEXT.name(), mysqlTextProcessor),
                Map.entry(MysqlColumnTypeEnum.GEOMETRY.name(), mariaDBGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.POINT.name(), mariaDBGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.LINESTRING.name(), mariaDBGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.POLYGON.name(), mariaDBGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.MULTIPOINT.name(), mariaDBGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.MULTILINESTRING.name(), mariaDBGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.MULTIPOLYGON.name(), mariaDBGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.GEOMETRYCOLLECTION.name(), mariaDBGeometryProcessor),
                Map.entry(MysqlColumnTypeEnum.VARBINARY.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.BLOB.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.LONGBLOB.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.TINYBLOB.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.MEDIUMBLOB.name(), mysqlVarBinaryProcessor),
                Map.entry(MysqlColumnTypeEnum.TIMESTAMP.name(), mariaDBTimestampProcessor),
                Map.entry(MysqlColumnTypeEnum.DATETIME.name(), mariaDBTimestampProcessor),
                Map.entry(MysqlColumnTypeEnum.YEAR.name(), new MariaDBYearProcessor()),
                Map.entry(MysqlColumnTypeEnum.BIT.name(), new MariaDBBitProcessor()),
                Map.entry(MysqlColumnTypeEnum.DECIMAL.name(), new MysqlDecimalProcessor()),
                Map.entry(MysqlColumnTypeEnum.BINARY.name(), new MysqlBinaryProcessor())
        );
    }

    public static DefaultValueProcessor getValueProcessor(String type) {
        return  PROCESSOR_MAP.get(type);
    }
}
