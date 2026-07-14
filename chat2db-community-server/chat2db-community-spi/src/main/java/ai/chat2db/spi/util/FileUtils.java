package ai.chat2db.spi.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileUtils {

    public static <T> T readJsonValue(Class<?> loaderClass, String path, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (InputStream inputStream = getResourceAsStream(loaderClass, path)) {
            return mapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON resource " + path + " as " + clazz.getName(), e);
        }
    }

    public static <T> List<T> readJsonValueAsList(Class<?> loaderClass, String path, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (InputStream inputStream = getResourceAsStream(loaderClass, path)) {
            return mapper.readValue(inputStream,
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON resource " + path + " as list of " + clazz.getName(), e);
        }
    }

    private static InputStream getResourceAsStream(Class<?> loaderClass, String path) {
        InputStream inputStream = loaderClass.getResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalStateException("JSON resource not found: " + path + " relative to " + loaderClass.getName());
        }
        return inputStream;
    }
}
