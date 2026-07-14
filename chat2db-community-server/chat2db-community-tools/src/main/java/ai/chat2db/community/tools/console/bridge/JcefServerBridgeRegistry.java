package ai.chat2db.community.tools.console.bridge;

public class JcefServerBridgeRegistry {

    private static volatile IJcefServerBridge bridge;

    private JcefServerBridgeRegistry() {
    }

    public static void register(IJcefServerBridge jcefServerBridge) {
        bridge = jcefServerBridge;
    }

    public static IJcefServerBridge getBridge() {
        IJcefServerBridge current = bridge;
        if (current == null) {
            throw new IllegalStateException("JCEF server bridge is not registered");
        }
        return current;
    }
}
