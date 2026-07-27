package ai.chat2db.community.jcef.terminal;

import ai.chat2db.community.jcef.context.JcefContext;
import ai.chat2db.community.jcef.enums.ActionTypeEnum;
import ai.chat2db.community.jcef.utils.CallJsFunctionUtil;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public final class TerminalSessionManager {
    private static final ConcurrentMap<String, TerminalSession> SESSIONS = new ConcurrentHashMap<>();

    private TerminalSessionManager() {
    }

    public static Map<String, Object> create(Path directory, int columns, int rows) throws IOException {
        return create(directory, columns, rows, "system");
    }

    public static Map<String, Object> create(Path directory, int columns, int rows, String shellId) throws IOException {
        return create(directory, columns, rows, resolveShell(shellId));
    }

    private static Map<String, Object> create(Path directory, int columns, int rows, ShellCommand shell) throws IOException {
        Path cwd = directory.toRealPath();
        if (!Files.isDirectory(cwd)) {
            throw new IllegalArgumentException("Terminal working directory is not available");
        }

        Map<String, String> environment = new HashMap<>(System.getenv());
        environment.put("TERM", "xterm-256color");
        environment.put("COLORTERM", "truecolor");
        environment.put("TERM_PROGRAM", "Chat2DB");
        environment.putIfAbsent("LANG", "en_US.UTF-8");
        if (applyColorEnvironment(environment)) {
            applyShellColorEnvironment(environment, shell);
        }

        PtyProcess process = new PtyProcessBuilder(shell.command().toArray(String[]::new))
                .setDirectory(cwd.toString())
                .setEnvironment(environment)
                .setInitialColumns(Math.max(columns, 20))
                .setInitialRows(Math.max(rows, 5))
                .setRedirectErrorStream(true)
                .setWindowsAnsiColorEnabled(true)
                .start();

        String sessionId = UUID.randomUUID().toString();
        TerminalSession session = new TerminalSession(sessionId, cwd, shell, process);
        SESSIONS.put(sessionId, session);
        session.startOutputPump();

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("cwd", cwd.toString());
        result.put("shell", shell.displayName());
        result.put("shellId", shell.id());
        return result;
    }

    static boolean applyColorEnvironment(Map<String, String> environment) {
        // Selecting a Chat2DB terminal theme is an explicit color opt-in. Keep
        // the override scoped to this child process and avoid exposing CLI tools
        // to the conflicting NO_COLOR + FORCE_COLOR combination.
        environment.remove("NO_COLOR");
        environment.put("FORCE_COLOR", "1");
        environment.put("CLICOLOR_FORCE", "1");
        environment.putIfAbsent("CLICOLOR", "1");
        environment.putIfAbsent("LSCOLORS", "ExGxBxDxCxEgEdxbxgxcxd");
        environment.putIfAbsent("LS_COLORS", "di=34:ln=36:so=35:pi=33:ex=32:bd=33;01:cd=33;01");
        return true;
    }

    public static Map<String, Object> duplicate(String sessionId, int columns, int rows) throws IOException {
        TerminalSession session = requireSession(sessionId);
        return create(session.cwd(), columns, rows, session.shell());
    }

    public static Map<String, Object> capabilities() {
        String os = normalizedOsName();
        List<Map<String, Object>> shells = new ArrayList<>();
        shells.add(shellOption("system", "System default", true));
        addShellOption(shells, "bash", "Bash", findCommand(os, "bash", "bash.exe"));
        addShellOption(shells, "zsh", "Zsh", findCommand(os, "zsh"));
        addShellOption(shells, "pwsh", "PowerShell 7", findCommand(os, "pwsh", "pwsh.exe"));
        if (isWindows(os)) {
            addShellOption(shells, "powershell", "Windows PowerShell", findCommand(os, "powershell.exe"));
            addShellOption(shells, "cmd", "Command Prompt", resolveCommandPrompt());
        }
        return Map.of(
                "os", isWindows(os) ? "windows" : os.contains("mac") ? "mac" : "linux",
                "shells", shells
        );
    }

    public static void write(String sessionId, String data) throws IOException {
        TerminalSession session = requireSession(sessionId);
        synchronized (session.output()) {
            session.output().write(data.getBytes(StandardCharsets.UTF_8));
            session.output().flush();
        }
    }

    public static void resize(String sessionId, int columns, int rows) {
        requireSession(sessionId).process().setWinSize(new WinSize(Math.max(columns, 20), Math.max(rows, 5)));
    }

    public static void attach(String sessionId) {
        requireSession(sessionId).attach();
    }

    public static Map<String, Object> status(String sessionId) {
        TerminalSession session = SESSIONS.get(sessionId);
        boolean alive = session != null && session.process().isAlive();
        boolean busy = alive && getAliveDescendants(session.process()).stream().anyMatch(ProcessHandle::isAlive);
        return Map.of("alive", alive, "busy", busy);
    }

    public static void kill(String sessionId) {
        TerminalSession session = SESSIONS.remove(sessionId);
        if (session != null) {
            getAliveDescendants(session.process()).forEach(ProcessHandle::destroy);
            session.process().destroy();
        }
    }

    private static List<ProcessHandle> getAliveDescendants(PtyProcess process) {
        try {
            return ProcessHandle.of(process.pid())
                    .map(handle -> handle.descendants().filter(ProcessHandle::isAlive).toList())
                    .orElseGet(List::of);
        } catch (UnsupportedOperationException | SecurityException exception) {
            log.debug("Unable to inspect terminal child processes", exception);
            return List.of();
        }
    }

    private static TerminalSession requireSession(String sessionId) {
        TerminalSession session = SESSIONS.get(sessionId);
        if (session == null || !session.process().isAlive()) {
            throw new IllegalArgumentException("Terminal session is not available");
        }
        return session;
    }

    private static ShellCommand resolveShell(String requestedShellId) {
        String shellId = requestedShellId == null || requestedShellId.isBlank()
                ? "system"
                : requestedShellId.toLowerCase(Locale.ROOT);
        String os = normalizedOsName();
        if ("system".equals(shellId)) {
            return resolveSystemShell(os);
        }
        return switch (shellId) {
            case "bash" -> unixShell("bash", "Bash", requireCommand(os, "Bash", "bash", "bash.exe"));
            case "zsh" -> unixShell("zsh", "Zsh", requireCommand(os, "Zsh", "zsh"));
            case "pwsh" -> powerShell("pwsh", "PowerShell 7", requireCommand(os, "PowerShell 7", "pwsh", "pwsh.exe"));
            case "powershell" -> powerShell(
                    "powershell",
                    "Windows PowerShell",
                    requireCommand(os, "Windows PowerShell", "powershell.exe")
            );
            case "cmd" -> new ShellCommand(
                    "cmd",
                    List.of(requireCommand(os, "Command Prompt", resolveCommandPrompt())),
                    "Command Prompt",
                    ShellFamily.CMD
            );
            default -> throw new IllegalArgumentException("Unsupported terminal shell: " + shellId);
        };
    }

    private static ShellCommand resolveSystemShell(String os) {
        if (isWindows(os)) {
            String pwsh = findCommand(os, "pwsh.exe", "pwsh");
            if (pwsh != null) {
                return powerShell("system", "PowerShell 7", pwsh);
            }
            String powerShell = findCommand(os, "powershell.exe");
            if (powerShell != null) {
                return powerShell("system", "Windows PowerShell", powerShell);
            }
            return new ShellCommand(
                    "system",
                    List.of(requireCommand(os, "Command Prompt", resolveCommandPrompt())),
                    "Command Prompt",
                    ShellFamily.CMD
            );
        }
        String configuredShell = System.getenv("SHELL");
        if (configuredShell != null && Files.isExecutable(Path.of(configuredShell))) {
            return unixShell("system", Path.of(configuredShell).getFileName().toString(), configuredShell);
        }
        String fallbackName = os.contains("mac") ? "zsh" : "bash";
        return unixShell("system", fallbackName, requireCommand(os, fallbackName, fallbackName));
    }

    private static void applyShellColorEnvironment(Map<String, String> environment, ShellCommand shell) {
        switch (shell.family()) {
            case BASH -> environment.put("PS1", "\\[\\033[34m\\]\\u@\\h\\[\\033[0m\\]:"
                    + "\\[\\033[32m\\]\\w\\[\\033[0m\\]\\$ ");
            case ZSH -> environment.put("PROMPT", "%F{blue}%n@%m%f:%F{green}%~%f %# ");
            case CMD -> environment.put("PROMPT", "$E[34m$P$E[0m$G$S");
            default -> {
                // PowerShell uses PSReadLine colors; other shells keep their native prompt configuration.
            }
        }
    }

    private static ShellCommand unixShell(String id, String displayName, String command) {
        String executableName = Path.of(command).getFileName().toString().toLowerCase(Locale.ROOT);
        ShellFamily family = executableName.contains("zsh")
                ? ShellFamily.ZSH
                : executableName.contains("bash") ? ShellFamily.BASH : ShellFamily.OTHER;
        return new ShellCommand(id, List.of(command, "-l"), displayName, family);
    }

    private static ShellCommand powerShell(String id, String displayName, String command) {
        String colorSetup = "if (Get-Command Set-PSReadLineOption -ErrorAction SilentlyContinue) { "
                + "Set-PSReadLineOption -Colors @{ "
                + "Command = 'Blue'; Parameter = 'Cyan'; String = 'Green'; "
                + "Operator = 'Yellow'; Variable = 'Magenta'; Number = 'Yellow' } }";
        return new ShellCommand(
                id,
                List.of(command, "-NoLogo", "-NoExit", "-Command", colorSetup),
                displayName,
                ShellFamily.POWERSHELL
        );
    }

    private static Map<String, Object> shellOption(String id, String label, boolean available) {
        return Map.of("id", id, "label", label, "available", available);
    }

    private static void addShellOption(List<Map<String, Object>> options, String id, String label, String command) {
        if (command != null) {
            options.add(shellOption(id, label, true));
        }
    }

    private static String requireCommand(String os, String label, String... candidates) {
        String command = findCommand(os, candidates);
        if (command == null) {
            throw new IllegalArgumentException(label + " is not installed or is not available in PATH");
        }
        return command;
    }

    private static String resolveCommandPrompt() {
        return System.getenv().getOrDefault("ComSpec", "cmd.exe");
    }

    private static String findCommand(String os, String... candidates) {
        for (String candidate : candidates) {
            Path directPath = Path.of(candidate);
            if (directPath.isAbsolute() && Files.isRegularFile(directPath) && Files.isExecutable(directPath)) {
                return directPath.toString();
            }
            String pathValue = System.getenv("PATH");
            if (pathValue == null || pathValue.isBlank()) {
                continue;
            }
            for (String pathEntry : pathValue.split(java.util.regex.Pattern.quote(File.pathSeparator))) {
                if (pathEntry.isBlank()) {
                    continue;
                }
                Path executable = Path.of(pathEntry, candidate);
                if (Files.isRegularFile(executable) && (isWindows(os) || Files.isExecutable(executable))) {
                    return executable.toString();
                }
            }
        }
        return null;
    }

    private static String normalizedOsName() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    }

    private static boolean isWindows(String os) {
        return os.contains("win");
    }

    private static void publish(String sessionId, ActionTypeEnum actionType, Map<String, Object> message) {
        ConsoleResult result = new ConsoleResult();
        result.setUuid(sessionId);
        result.setActionType(actionType.getName());
        result.setMessage(message);
        CallJsFunctionUtil.callHandleJavaMessage(
                JcefContext.getInstance().getBrowser_(),
                JSON.toJSONString(result)
        );
    }

    private enum ShellFamily {
        BASH,
        ZSH,
        POWERSHELL,
        CMD,
        OTHER
    }

    private record ShellCommand(String id, List<String> command, String displayName, ShellFamily family) {
    }

    private static final class TerminalSession {
        private static final int MAX_PENDING_CHARACTERS = 1024 * 1024;
        private final String id;
        private final Path cwd;
        private final ShellCommand shell;
        private final PtyProcess process;
        private final StringBuilder pendingOutput = new StringBuilder();
        private boolean attached;

        private TerminalSession(String id, Path cwd, ShellCommand shell, PtyProcess process) {
            this.id = id;
            this.cwd = cwd;
            this.shell = shell;
            this.process = process;
        }

        Path cwd() {
            return cwd;
        }

        ShellCommand shell() {
            return shell;
        }

        PtyProcess process() {
            return process;
        }

        OutputStream output() {
            return process.getOutputStream();
        }

        synchronized void attach() {
            attached = true;
            if (!pendingOutput.isEmpty()) {
                publish(id, ActionTypeEnum.TERMINAL_OUTPUT, Map.of("data", pendingOutput.toString()));
                pendingOutput.setLength(0);
            }
        }

        synchronized void publishOutput(String data) {
            if (attached) {
                publish(id, ActionTypeEnum.TERMINAL_OUTPUT, Map.of("data", data));
                return;
            }
            pendingOutput.append(data);
            if (pendingOutput.length() > MAX_PENDING_CHARACTERS) {
                pendingOutput.delete(0, pendingOutput.length() - MAX_PENDING_CHARACTERS);
            }
        }

        void startOutputPump() {
            Thread outputThread = new Thread(() -> {
                try (InputStreamReader reader = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)) {
                    char[] buffer = new char[4096];
                    int count;
                    while ((count = reader.read(buffer)) >= 0) {
                        if (count > 0) {
                            publishOutput(new String(buffer, 0, count));
                        }
                    }
                } catch (IOException exception) {
                    if (process.isAlive()) {
                        log.warn("Failed to read terminal session {}", id, exception);
                    }
                } finally {
                    int exitCode = -1;
                    try {
                        exitCode = process.waitFor();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    }
                    SESSIONS.remove(id);
                    publish(id, ActionTypeEnum.TERMINAL_EXIT, Map.of("exitCode", exitCode));
                }
            }, "chat2db-terminal-" + id);
            outputThread.setDaemon(true);
            outputThread.start();
        }
    }
}
