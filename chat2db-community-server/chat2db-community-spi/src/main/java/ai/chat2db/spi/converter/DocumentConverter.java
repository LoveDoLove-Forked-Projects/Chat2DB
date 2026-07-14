package ai.chat2db.spi.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.bson.types.Binary;

import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public final class DocumentConverter {

    private DocumentConverter() {
    }

    public static LinkedHashMap<String, Object> object2map(Object obj) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (obj == null) {
            return map;
        }
        if (ClassUtils.isPrimitiveOrWrapper(obj.getClass()) || String.class.equals(obj.getClass())) {
            map.put("result", obj);
            return map;
        }
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) obj).entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                map.put(entry.getKey(), null);
            } else if (ClassUtils.isPrimitiveOrWrapper(value.getClass()) || String.class.equals(value.getClass())) {
                map.put(entry.getKey(), value);
            } else if (entry.getValue() instanceof Map) {
                map.put(entry.getKey(), object2map(entry.getValue()));
            } else if (value instanceof byte[] bytes) {
                map.put(entry.getKey(), String.format("(byte) %d bytes", bytes.length));
            } else if ((value instanceof Binary) || (Binary.class.getName().equals(value.getClass().getName()))) {
                map.put(entry.getKey(), binary2string(value));
            } else if (value instanceof Blob blob) {
                map.put(entry.getKey(), blob2string(blob));
            } else {
                map.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return map;
    }

    private static String binary2string(Object value) {
        try {
            Method getDataMethod = value.getClass().getMethod("getData");
            byte[] data = (byte[]) getDataMethod.invoke(value);
            String base64Data = Base64.getEncoder().encodeToString(data);
            return String.format("BinData(%d, \"%s\")",
                    ((Byte) value.getClass().getMethod("getType").invoke(value)), base64Data);
        } catch (Exception e) {
            log.error("Failed to convert Binary to hex string", e);
            return null;
        }
    }

    private static String blob2string(Blob blob) {
        try {
            int blobLength = (int) blob.length();
            return String.format("(BLOB) %d bytes", blobLength);
        } catch (SQLException e) {
            log.error("Failed to convert Blob to hex string", e);
            return null;
        }
    }
}
