package ai.chat2db.community.domain.api.enums.value;

import ai.chat2db.community.tools.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public enum CellValueFormatEnum {
    AUTO,
    TEXT,
    HEX,
    BASE64,
    RAW;

    public static CellValueFormatEnum fromRequest(String value) {
        if (StringUtils.isBlank(value)) {
            return AUTO;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("largeCellValue.unsupportedFormat", new Object[]{value}, e);
        }
    }

    public String code() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isBase64() {
        return this == BASE64;
    }

    public boolean isEncoded() {
        return this == HEX || this == BASE64;
    }

    public CellValueFormatEnum forRead() {
        return this == AUTO || this == RAW ? HEX : this;
    }

    public CellValueFormatEnum forDownload() {
        return this == AUTO ? RAW : this;
    }
}
