package ai.chat2db.plugin.gbase8s;

import ai.chat2db.plugin.generic.GenericDBManager;
import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

@Slf4j
public class GBase8sDBManager extends GenericDBManager implements IDbManager {

    @Override
    public Connection getConnection(ConnectInfo connectInfo) {
        String url = connectInfo.getUrl();
        String service = connectInfo.getServiceName();
        if(StringUtils.isNotBlank(service)){
            url = url + ":" + "GBASEDBTSERVER=" + service;
        }
        connectInfo.setUrl(url);
        Connection connection = super.getConnection(connectInfo);
        return connection;
    }
}
