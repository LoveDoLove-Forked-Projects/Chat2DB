package ai.chat2db.community.domain.core.impl.ncx;

import ai.chat2db.community.domain.core.enums.ncx.VersionEnum;
import ai.chat2db.community.domain.core.impl.ncx.cipher.CommonCipher;
import ai.chat2db.community.domain.core.impl.ncx.cipher.Navicat11Cipher;
import ai.chat2db.community.domain.core.impl.ncx.cipher.Navicat12Cipher;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CipherFactory {


    private static final Map<String, CommonCipher> REPORT_POOL = new ConcurrentHashMap<>(0);

    static {
        REPORT_POOL.put(VersionEnum.native11.name(), new Navicat11Cipher());
        REPORT_POOL.put(VersionEnum.navicat12more.name(), new Navicat12Cipher());
    }


    @SneakyThrows
    public static CommonCipher get(String type) {
        CommonCipher cipher = REPORT_POOL.get(type);
        if (cipher == null) {
            throw new ClassNotFoundException("no CommonCipher was found");
        } else {
            return cipher;
        }
    }
}
