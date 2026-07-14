package ai.chat2db.plugin.mysql.value.sub;

import ai.chat2db.plugin.mysql.value.template.MysqlDmlValueTemplate;
import ai.chat2db.spi.DefaultValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class MysqlGeometryProcessor extends DefaultValueProcessor {


    private static final Logger log = LoggerFactory.getLogger(MysqlGeometryProcessor.class);

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return MysqlDmlValueTemplate.wrapGeometry(dataValue.getValue());
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        try {
            InputStream inputStream = dataValue.getBinaryStream();
            Geometry dbGeometry = null;
            if (inputStream != null) {
                byte[] buffer = new byte[255];

                int bytesRead = 0;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                byte[] geometryAsBytes = baos.toByteArray();

                if (geometryAsBytes.length < 5) {
                    throw new Exception("Invalid geometry inputStream - less than five bytes");
                }
                byte[] sridBytes = new byte[4];
                System.arraycopy(geometryAsBytes, 0, sridBytes, 0, 4);
                boolean bigEndian = (geometryAsBytes[4] == 0x00);

                int srid = 0;
                if (bigEndian) {
                    for (int i = 0; i < sridBytes.length; i++) {
                        srid = (srid << 8) + (sridBytes[i] & 0xff);
                    }
                } else {
                    for (int i = 0; i < sridBytes.length; i++) {
                        srid += (sridBytes[i] & 0xff) << (8 * i);
                    }
                }
                WKBReader wkbReader = new WKBReader();
                byte[] wkb = new byte[geometryAsBytes.length - 4];
                System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
                dbGeometry = wkbReader.read(wkb);
                dbGeometry.setSRID(srid);
            }
            return dbGeometry != null ? dbGeometry.toString() : null;
        } catch (Exception e) {
            log.warn("Error converting database geometry", e);
            return dataValue.getStringValue();
        }
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return MysqlDmlValueTemplate.wrapGeometry(convertJDBCValueByType(dataValue));
    }

}
