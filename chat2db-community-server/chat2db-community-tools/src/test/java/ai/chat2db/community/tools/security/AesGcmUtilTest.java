package ai.chat2db.community.tools.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AesGcmUtilTest {

    private static final String KEY = key(1);
    private static final String OTHER_KEY = key(2);

    @AfterEach
    void clearConfiguredKey() {
        System.clearProperty(AesGcmUtil.KEY_PROPERTY);
    }

    @Test
    void encryptsAndDecryptsValue() {
        AesGcmUtil util = new AesGcmUtil(KEY);

        String encrypted = util.encrypt("chat2db-password");

        assertEquals("chat2db-password", util.decrypt(encrypted));
    }

    @Test
    void separatesDatasourcePasswordsFromAiModelApiKeys() {
        AesGcmUtil util = new AesGcmUtil(KEY);

        String datasourceCiphertext = util.encrypt("shared-secret");
        String aiModelCiphertext = util.encryptAiModelApiKey("shared-secret");

        assertEquals("shared-secret", util.decrypt(datasourceCiphertext));
        assertEquals("shared-secret", util.decryptAiModelApiKey(aiModelCiphertext));
        assertDecryptFailure(() -> util.decryptAiModelApiKey(datasourceCiphertext));
        assertDecryptFailure(() -> util.decrypt(aiModelCiphertext));
    }

    @Test
    void usesRandomNonceForEveryEncryption() {
        AesGcmUtil util = new AesGcmUtil(KEY);

        String first = util.encrypt("same-password");
        String second = util.encrypt("same-password");

        assertNotEquals(first, second);
        assertEquals("same-password", util.decrypt(first));
        assertEquals("same-password", util.decrypt(second));
    }

    @Test
    void rejectsWrongKeyWithoutExposingCause() {
        String encrypted = new AesGcmUtil(KEY).encrypt("secret-password");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> new AesGcmUtil(OTHER_KEY).decrypt(encrypted));

        assertEquals("Unable to decrypt Community local storage value", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void rejectsTamperedAndTruncatedValues() {
        AesGcmUtil util = new AesGcmUtil(KEY);
        byte[] payload = Base64.getDecoder().decode(util.encrypt("secret-password"));
        payload[payload.length - 1] ^= 1;

        assertDecryptFailure(util, Base64.getEncoder().encodeToString(payload));
        assertDecryptFailure(util, Base64.getEncoder().encodeToString(new byte[27]));
    }

    @Test
    void preservesEmptyValuesAndEncryptsWhitespace() {
        AesGcmUtil util = new AesGcmUtil(KEY);

        assertNull(util.encrypt(null));
        assertEquals("", util.encrypt(""));
        assertNull(util.decrypt(null));
        assertEquals("", util.decrypt(""));

        String encryptedWhitespace = util.encrypt("  ");
        assertNotEquals("  ", encryptedWhitespace);
        assertEquals("  ", util.decrypt(encryptedWhitespace));
    }

    @Test
    void supportsConcurrentEncryption() {
        AesGcmUtil util = new AesGcmUtil(KEY);

        List<String> encrypted = IntStream.range(0, 64)
                .parallel()
                .mapToObj(index -> util.encrypt("same-password"))
                .toList();

        assertEquals(64, encrypted.stream().distinct().count());
        assertTrue(encrypted.stream().allMatch(value -> "same-password".equals(util.decrypt(value))));
    }

    @Test
    void readsAndFreezesConfiguredKeyFromSystemProperty() {
        System.setProperty(AesGcmUtil.KEY_PROPERTY, KEY);

        AesGcmUtil first = AesGcmUtil.configured();
        System.setProperty(AesGcmUtil.KEY_PROPERTY, OTHER_KEY);
        AesGcmUtil second = AesGcmUtil.configured();

        assertSame(first, second);
        assertEquals("password", second.decrypt(first.encrypt("password")));
    }

    @Test
    void requiresBase64Encoded256BitKey() {
        assertThrows(IllegalStateException.class, () -> new AesGcmUtil(null));
        assertThrows(IllegalStateException.class, () -> new AesGcmUtil("not-base64"));
        assertThrows(IllegalStateException.class, () -> new AesGcmUtil(keyBytes(16, 1)));
    }

    private static void assertDecryptFailure(AesGcmUtil util, String value) {
        assertDecryptFailure(() -> util.decrypt(value));
    }

    private static void assertDecryptFailure(Runnable decrypt) {
        IllegalStateException exception = assertThrows(IllegalStateException.class, decrypt::run);
        assertEquals("Unable to decrypt Community local storage value", exception.getMessage());
        assertNull(exception.getCause());
    }

    private static String key(int value) {
        return keyBytes(32, value);
    }

    private static String keyBytes(int length, int value) {
        byte[] bytes = new byte[length];
        Arrays.fill(bytes, (byte) value);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
