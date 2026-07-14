package ai.chat2db.community.domain.api.enums.file;

import ai.chat2db.community.tools.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

public enum ConfigFileTypeEnum {
    NCX,
    DBP,
    JSON;

    public static ConfigFileTypeEnum fromExtension(String extension) {
        for (ConfigFileTypeEnum type : values()) {
            if (StringUtils.equalsIgnoreCase(type.name(), extension)) {
                return type;
            }
        }
        throw new BusinessException("file.type.unsupported");
    }
}
