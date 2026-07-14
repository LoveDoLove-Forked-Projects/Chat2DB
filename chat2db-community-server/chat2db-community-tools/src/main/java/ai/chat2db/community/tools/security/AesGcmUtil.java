package ai.chat2db.community.tools.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesGcmUtil {

    public static final String KEY_PROPERTY = "chat2db.community.encryption-key";
    public static final String KEY_ENV = "CHAT2DB_COMMUNITY_ENCRYPTION_KEY";
    public static final String KEY_FILE_PROPERTY = "chat2db.community.encryption-key-file";
    public static final String KEY_FILE_ENV = "CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";
    private static final int KEY_LENGTH_BYTES = 32;
    private static final int NONCE_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BYTES = 16;
    private static final int TAG_LENGTH_BITS = TAG_LENGTH_BYTES * Byte.SIZE;
    private static final byte[] DATASOURCE_PASSWORD_AAD = "chat2db-community-datasource-password"
            .getBytes(StandardCharsets.UTF_8);
    private static final byte[] AI_MODEL_API_KEY_AAD = "chat2db-community-ai-model-api-key"
            .getBytes(StandardCharsets.UTF_8);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static volatile AesGcmUtil configuredInstance;

    private final SecretKey key;

    public AesGcmUtil(String encodedKey) {
        this.key = parseKey(encodedKey);
    }

    public static AesGcmUtil configured() {
        AesGcmUtil result = configuredInstance;
        if (result == null) {
            synchronized (AesGcmUtil.class) {
                result = configuredInstance;
                if (result == null) {
                    result = new AesGcmUtil(CommunityEncryptionKeyStore.resolve());
                    configuredInstance = result;
                }
            }
        }
        return result;
    }

    public String encrypt(String data) {
        return encrypt(data, DATASOURCE_PASSWORD_AAD);
    }

    public String decrypt(String data) {
        return decrypt(data, DATASOURCE_PASSWORD_AAD);
    }

    public String encryptAiModelApiKey(String data) {
        return encrypt(data, AI_MODEL_API_KEY_AAD);
    }

    public String decryptAiModelApiKey(String data) {
        return decrypt(data, AI_MODEL_API_KEY_AAD);
    }

    private String encrypt(String data, byte[] aad) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        byte[] nonce = new byte[NONCE_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(nonce);
        try {
            Cipher cipher = newCipher(Cipher.ENCRYPT_MODE, nonce, aad);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[nonce.length + encrypted.length];
            System.arraycopy(nonce, 0, payload, 0, nonce.length);
            System.arraycopy(encrypted, 0, payload, nonce.length, encrypted.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to encrypt Community local storage value");
        }
    }

    private String decrypt(String data, byte[] aad) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        try {
            byte[] payload = Base64.getDecoder().decode(data);
            if (payload.length < NONCE_LENGTH_BYTES + TAG_LENGTH_BYTES) {
                throw decryptFailure();
            }
            byte[] nonce = Arrays.copyOfRange(payload, 0, NONCE_LENGTH_BYTES);
            byte[] encrypted = Arrays.copyOfRange(payload, NONCE_LENGTH_BYTES, payload.length);
            Cipher cipher = newCipher(Cipher.DECRYPT_MODE, nonce, aad);
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw decryptFailure();
        }
    }

    private Cipher newCipher(int mode, byte[] nonce, byte[] aad) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(mode, key, new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
        cipher.updateAAD(aad);
        return cipher;
    }

    private static SecretKey parseKey(String encodedKey) {
        if (encodedKey == null || encodedKey.isBlank()) {
            throw invalidKey();
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(encodedKey.trim());
        } catch (IllegalArgumentException exception) {
            throw invalidKey();
        }
        try {
            if (keyBytes.length != KEY_LENGTH_BYTES) {
                throw invalidKey();
            }
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
        }
    }

    private static IllegalStateException invalidKey() {
        return new IllegalStateException(
                "Community encryption key must be a Base64-encoded 32-byte value configured through "
                        + KEY_PROPERTY + ", " + KEY_ENV + ", or the Community encryption key file");
    }

    private static IllegalStateException decryptFailure() {
        return new IllegalStateException("Unable to decrypt Community local storage value");
    }
}
