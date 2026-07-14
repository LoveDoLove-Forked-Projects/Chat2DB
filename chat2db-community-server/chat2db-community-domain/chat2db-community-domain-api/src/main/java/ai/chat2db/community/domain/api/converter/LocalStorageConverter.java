package ai.chat2db.community.domain.api.converter;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.Map;
import java.util.function.Supplier;

public final class LocalStorageConverter {

    private LocalStorageConverter() {
    }

    public static <T> Long getId(T data) {
        try {
            return data == null ? null : (Long) PropertyUtils.getProperty(data, "id");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Long ensureId(T data, Supplier<Long> idGenerator) {
        Long id = getId(data);
        if (id != null) {
            return id;
        }
        id = idGenerator.get();
        setId(data, id);
        return id;
    }

    public static <T> void setId(T data, Long id) {
        try {
            PropertyUtils.setProperty(data, "id", id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T mergeNotNullProperties(T before, T update) {
        if (before == null) {
            return update;
        }
        if (update == null) {
            return before;
        }
        try {
            Map<String, Object> updateMap = PropertyUtils.describe(update);
            for (Map.Entry<String, Object> entry : updateMap.entrySet()) {
                if (entry.getValue() != null) {
                    PropertyUtils.setProperty(before, entry.getKey(), entry.getValue());
                }
            }
            return before;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
