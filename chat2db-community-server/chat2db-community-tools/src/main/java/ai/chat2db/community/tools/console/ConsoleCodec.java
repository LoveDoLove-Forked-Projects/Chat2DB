package ai.chat2db.community.tools.console;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class ConsoleCodec {

    private static final String CHAT2DB_IPC_REQUEST = "CHAT2DB_IPC_REQUEST:";
    private static final String CHAT2DB_IPC_REQUEST_END = ":CHAT2DB_IPC_REQUEST_END";

    public static final String CHAT2DB_IPC_RESPONSE = "CHAT2DB_IPC_RESPONSE:";
    public static final String CHAT2DB_IPC_RESPONSE_END = ":CHAT2DB_IPC_RESPONSE_END";
    public static final String CHAT2DB_IPC_RESPONSE_SERVICE_STATUS_SUCCESS =
            "CHAT2DB_IPC_RESPONSE_SERVICE_STATUS_SUCCESS";

    public static final String REQUEST_REGEX = CHAT2DB_IPC_REQUEST + "(.*?)" + CHAT2DB_IPC_REQUEST_END;

    private ConsoleCodec() {
    }

    public static String compress(String data) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (GzipCompressorOutputStream gzipOutputStream = new GzipCompressorOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            }
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            log.error("compress error", e);
        }
        return data;
    }

    public static String decompress(String data) {
        try {
            byte[] compressedData = Base64.getDecoder().decode(data);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
            try (GzipCompressorInputStream gzipInputStream = new GzipCompressorInputStream(byteArrayInputStream);
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = gzipInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("decompressFromBase64 error", e);
        }
        return data;
    }
}
