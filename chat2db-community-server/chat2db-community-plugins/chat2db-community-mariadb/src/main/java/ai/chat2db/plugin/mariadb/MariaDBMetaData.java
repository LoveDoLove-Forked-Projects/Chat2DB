package ai.chat2db.plugin.mariadb;


import ai.chat2db.plugin.mariadb.value.MariaDBValueProcessor;
import ai.chat2db.plugin.mysql.MysqlMetaData;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IValueProcessor;

public class MariaDBMetaData extends MysqlMetaData implements IDbMetaData {


    @Override
    public IValueProcessor getValueProcessor() {
        return new MariaDBValueProcessor();
    }
}
