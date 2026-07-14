package ai.chat2db.community.domain.api.model.value;

import ai.chat2db.community.domain.api.model.metadata.DataType;
import com.google.common.io.BaseEncoding;
import lombok.Data;


@Data
public class SQLDataValue {
    private String value;
    private DataType dataType;

    public String getDateTypeName() {
        return dataType.getDataTypeName();
    }

    public int getPrecision() {
        return dataType.getPrecision();
    }

    public int getScale() {
        return dataType.getScale();
    }

    public String getBlobHexString() {
        return BaseEncoding.base16().encode(value.getBytes());
    }
}
