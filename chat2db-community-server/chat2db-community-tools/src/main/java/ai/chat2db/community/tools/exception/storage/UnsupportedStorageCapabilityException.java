package ai.chat2db.community.tools.exception.storage;

public class UnsupportedStorageCapabilityException extends RuntimeException {

    public UnsupportedStorageCapabilityException(String message) {
        super(message);
    }

    public static UnsupportedStorageCapabilityException forCapability(String capability) {
        return new UnsupportedStorageCapabilityException(
                "Current storage mode does not support workspace capability: " + capability);
    }
}
