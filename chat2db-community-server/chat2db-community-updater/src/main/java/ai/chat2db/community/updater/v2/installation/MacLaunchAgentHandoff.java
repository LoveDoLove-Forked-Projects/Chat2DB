package ai.chat2db.community.updater.v2.installation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Starts the desktop update helper as a per-transaction macOS LaunchAgent.
 *
 * <p>A helper spawned as a plain child process of the application does not
 * survive the handoff: the application exits about 150 ms after spawning it,
 * while the helper needs seconds to start its JVM, so the helper is reclaimed
 * together with the application before it can switch anything. Running the
 * helper under launchd removes that dependency. {@code AbandonProcessGroup}
 * additionally keeps the application the helper relaunches alive after the
 * helper itself exits.</p>
 *
 * <p>{@code launchctl submit} is deliberately not used: launchd kills the
 * remaining processes of a submitted job's process group when its main process
 * exits, which would kill the relaunched application.</p>
 */
public final class MacLaunchAgentHandoff {

    static final String LABEL_PREFIX = "com.chat2db.updater.";
    static final String AGENT_SUFFIX = ".plist";

    /**
     * An agent written moments ago may belong to a transaction that is still
     * starting in another product, so it is not treated as stale yet.
     */
    private static final Duration STALE_AGENT_AGE = Duration.ofMinutes(10L);
    private static final Duration LAUNCHCTL_TIMEOUT = Duration.ofSeconds(15L);

    private final Path launchAgentsDirectory;
    private final String userId;
    private final CommandRunner runner;

    public MacLaunchAgentHandoff(Path launchAgentsDirectory, String userId, CommandRunner runner) {
        this.launchAgentsDirectory = launchAgentsDirectory;
        this.userId = userId;
        this.runner = runner;
    }

    public static MacLaunchAgentHandoff forCurrentUser(Path homeDirectory, CommandRunner runner)
            throws Exception {
        return new MacLaunchAgentHandoff(
            homeDirectory.resolve("Library").resolve("LaunchAgents"),
            currentUserId(runner),
            runner
        );
    }

    /**
     * A wedged launchd must not block the handoff: every launchctl call is bounded,
     * and a timeout is reported as a failure so the caller can fall back.
     */
    public static CommandRunner processRunner() {
        return command -> {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            if (!process.waitFor(LAUNCHCTL_TIMEOUT.toSeconds(), TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor(LAUNCHCTL_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
                return new CommandResult(-1, "launchctl timed out after " + LAUNCHCTL_TIMEOUT.toSeconds() + "s");
            }
            String output = new String(process.getInputStream().readAllBytes());
            return new CommandResult(process.exitValue(), output);
        };
    }

    static String currentUserId(CommandRunner runner) throws Exception {
        CommandResult result = runner.run(List.of("/usr/bin/id", "-u"));
        String userId = result.output() == null ? "" : result.output().trim();
        return userId.isEmpty() ? "-1" : userId;
    }

    static String label(String transactionId) {
        if (transactionId == null || !transactionId.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException("Update transaction id contains unsafe label characters");
        }
        return LABEL_PREFIX + transactionId;
    }

    public Path agentFile(String transactionId) {
        return launchAgentsDirectory.resolve(label(transactionId) + AGENT_SUFFIX);
    }

    /** The agent file of a transaction, computed without touching launchd. */
    public static Path agentFileFor(Path homeDirectory, String transactionId) {
        return homeDirectory.resolve("Library").resolve("LaunchAgents")
            .resolve(label(transactionId) + AGENT_SUFFIX);
    }

    /**
     * Deletes the agent file of a finished transaction. Deleting the file is
     * enough: the job has already run, a missing plist is not loaded at the next
     * login, and unloading the job would kill the application it relaunched.
     */
    public static boolean removeAgentFile(Path homeDirectory, String transactionId) {
        try {
            return Files.deleteIfExists(agentFileFor(homeDirectory, transactionId));
        } catch (Exception unremovable) {
            return false;
        }
    }

    /**
     * Writes the agent for this transaction and loads it. The helper then runs
     * independently of the application that handed the update over.
     */
    public int bootstrap(String transactionId, List<String> helperCommand, Path workDirectory,
            Path stdout, Path stderr) throws Exception {
        // A job left over from a crashed attempt of the same transaction would make
        // bootstrap fail, so it is unloaded before the agent is written again.
        bootout(transactionId);
        Path agent = agentFile(transactionId);
        Path parent = agent.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(agent, plist(label(transactionId), helperCommand, workDirectory, stdout, stderr));
        int loadExit = runner.run(List.of("/bin/launchctl", "bootstrap", "gui/" + userId, agent.toString()))
            .exitCode();
        if (loadExit != 0) {
            return loadExit;
        }
        // The agent is loaded without RunAtLoad and started explicitly: a plist that
        // survives a crash then does nothing at the next login.
        return runner.run(List.of("/bin/launchctl", "kickstart", "gui/" + userId + "/" + label(transactionId)))
            .exitCode();
    }

    /**
     * Unloads and deletes agents left behind by earlier transactions. Keeping
     * them would accumulate plists and could replay an outdated helper plan. A
     * job that is still running is left alone: another product may be updating
     * from the same account at the same time.
     */
    public List<String> bootoutStale(String keepTransactionId) throws Exception {
        List<String> removed = new ArrayList<>();
        if (!Files.isDirectory(launchAgentsDirectory)) {
            return removed;
        }
        String keepLabel = keepTransactionId == null ? null : label(keepTransactionId);
        try (var entries = Files.list(launchAgentsDirectory)) {
            for (Path entry : entries.sorted().toList()) {
                String name = entry.getFileName().toString();
                if (!name.startsWith(LABEL_PREFIX) || !name.endsWith(AGENT_SUFFIX)) {
                    continue;
                }
                String label = name.substring(0, name.length() - AGENT_SUFFIX.length());
                if (label.equals(keepLabel) || isRunning(label) || isRecent(entry)) {
                    continue;
                }
                runner.run(List.of("/bin/launchctl", "bootout", "gui/" + userId + "/" + label));
                Files.deleteIfExists(entry);
                removed.add(label);
            }
        }
        return removed;
    }

    /**
     * Unloads this transaction's agent. It is used when the helper never
     * acknowledged: the helper cannot switch anything before the application
     * exits, so unloading it prevents a later switch the user was told failed.
     * A non-zero exit code also means "this job was not loaded", which is normal
     * on a first handoff.
     */
    public int bootout(String transactionId) throws Exception {
        int exitCode = runner.run(
            List.of("/bin/launchctl", "bootout", "gui/" + userId + "/" + label(transactionId)))
            .exitCode();
        Files.deleteIfExists(agentFile(transactionId));
        return exitCode;
    }

    private static boolean isRecent(Path agent) {
        try {
            return Files.getLastModifiedTime(agent).toInstant()
                .isAfter(Instant.now().minus(STALE_AGENT_AGE));
        } catch (Exception unreadable) {
            // Never unload an agent whose age cannot be read.
            return true;
        }
    }

    private boolean isRunning(String label) {
        try {
            CommandResult result = runner.run(List.of("/bin/launchctl", "list", label));
            return result.output() != null && result.output().contains("\"PID\"");
        } catch (Exception unreadable) {
            // Never unload a job that cannot be inspected.
            return true;
        }
    }

    static String plist(String label, List<String> helperCommand, Path workDirectory,
            Path stdout, Path stderr) {
        StringBuilder arguments = new StringBuilder();
        for (String argument : helperCommand) {
            arguments.append("    <string>").append(xmlEscape(argument)).append("</string>\n");
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" "
            + "\"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
            + "<plist version=\"1.0\">\n"
            + "<dict>\n"
            + "  <key>Label</key>\n"
            + "  <string>" + xmlEscape(label) + "</string>\n"
            + "  <key>ProgramArguments</key>\n"
            + "  <array>\n"
            + arguments
            + "  </array>\n"
            + "  <key>WorkingDirectory</key>\n"
            + "  <string>" + xmlEscape(workDirectory.toString()) + "</string>\n"
            + "  <key>StandardOutPath</key>\n"
            + "  <string>" + xmlEscape(stdout.toString()) + "</string>\n"
            + "  <key>StandardErrorPath</key>\n"
            + "  <string>" + xmlEscape(stderr.toString()) + "</string>\n"
            + "  <key>RunAtLoad</key>\n"
            + "  <false/>\n"
            + "  <key>KeepAlive</key>\n"
            + "  <false/>\n"
            + "  <key>AbandonProcessGroup</key>\n"
            + "  <true/>\n"
            + "  <key>LimitLoadToSessionType</key>\n"
            + "  <string>Aqua</string>\n"
            + "</dict>\n"
            + "</plist>\n";
    }

    private static String xmlEscape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @FunctionalInterface
    public interface CommandRunner {
        CommandResult run(List<String> command) throws Exception;
    }

    public record CommandResult(int exitCode, String output) {
    }
}
