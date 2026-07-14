package ai.chat2db.spi.util;

import cn.hutool.core.util.DesensitizedUtil;
import org.apache.commons.lang3.StringUtils;

public class DesensitizedUtils {

    public static String desensitize(String value, String desensitizeType) {
        if(StringUtils.isEmpty(desensitizeType)) {
            return value;
        }
        if(StringUtils.isEmpty(value)) {
            return value;
        }
        DesensitizedUtil.DesensitizedType type = DesensitizedUtil.DesensitizedType.valueOf(desensitizeType);
        return DesensitizedUtil.desensitized(value, type);
    }
}
