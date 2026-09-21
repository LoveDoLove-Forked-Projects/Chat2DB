package ai.chat2db.community.jcef.update;

/**
 * Result of an update check.
 *
 * <p>{@link State#READY_TO_INSTALL} means a package has already been downloaded and
 * staged, possibly in an earlier session, so the user can install it without
 * downloading it again.</p>
 */
public record DesktopUpdateCheckResult(State state, String version) {

    public enum State {
        AVAILABLE,
        NOT_AVAILABLE,
        READY_TO_INSTALL
    }

    /** Compatibility constructor for callers that only distinguish available from not available. */
    public DesktopUpdateCheckResult(boolean needsUpdate, String version) {
        this(needsUpdate ? State.AVAILABLE : State.NOT_AVAILABLE, version);
    }

    public static DesktopUpdateCheckResult available(String version) {
        return new DesktopUpdateCheckResult(State.AVAILABLE, version);
    }

    public static DesktopUpdateCheckResult notAvailable() {
        return new DesktopUpdateCheckResult(State.NOT_AVAILABLE, "");
    }

    public static DesktopUpdateCheckResult readyToInstall(String version) {
        return new DesktopUpdateCheckResult(State.READY_TO_INSTALL, version);
    }

    /**
     * Whether this installation still has to install an update: either one that was
     * discovered now or one that is already downloaded and waiting for the restart.
     */
    public boolean needsUpdate() {
        return state != State.NOT_AVAILABLE;
    }
}
