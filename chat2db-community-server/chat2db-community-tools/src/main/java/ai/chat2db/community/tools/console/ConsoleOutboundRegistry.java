package ai.chat2db.community.tools.console;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleOutboundRegistry {

    private static volatile IConsoleOutbound outbound;

    private ConsoleOutboundRegistry() {
    }

    public static void register(IConsoleOutbound consoleOutbound) {
        outbound = consoleOutbound;
    }

    public static void send(String message) {
        IConsoleOutbound current = outbound;
        if (current == null) {
            log.debug("Console outbound is not registered, skip message");
            return;
        }
        current.send(message);
    }
}
