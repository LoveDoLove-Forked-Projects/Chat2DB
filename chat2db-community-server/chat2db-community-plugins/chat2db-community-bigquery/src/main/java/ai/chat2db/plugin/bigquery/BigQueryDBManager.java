package ai.chat2db.plugin.bigquery;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.DefaultDBManager;
import ai.chat2db.community.domain.api.model.datasource.KeyValue;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BigQueryDBManager extends DefaultDBManager implements IDbManager {

    @Override
    public Connection getConnection(ConnectInfo connectInfo) {
        List<KeyValue> keyValues = connectInfo.getExtendInfo();
        if(keyValues == null){
            keyValues =new ArrayList<>();
        }
        if(StringUtils.isNotBlank(connectInfo.getProject())){
            KeyValue keyValue = new KeyValue();
            keyValue.setKey("ProjectId");
            keyValue.setValue(connectInfo.getProject());
            keyValues.add(keyValue);
        }
        if(StringUtils.isNotBlank(connectInfo.getEmail())){
            KeyValue keyValue = new KeyValue();
            keyValue.setKey("OAuthServiceAcctEmail");
            keyValue.setValue(connectInfo.getEmail());
            keyValues.add(keyValue);
        }
        if(StringUtils.isNotBlank(connectInfo.getKeyfile())){
            KeyValue keyValue = new KeyValue();
            keyValue.setKey("OAuthType");
            keyValue.setValue("0");
            keyValues.add(keyValue);

            KeyValue keyValue1 = new KeyValue();
            keyValue1.setKey("OAuthPvtKeyPath");
            keyValue1.setValue(connectInfo.getKeyfile());
            keyValues.add(keyValue1);
        }
        connectInfo.setExtendInfo(keyValues);
        Connection connection = super.getConnection(connectInfo);
        return connection;
    }

}
