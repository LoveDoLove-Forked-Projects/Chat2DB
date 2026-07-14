package ai.chat2db.community.jcef.event;
public class OpenFileManagerEvent implements IEvent {
    private final String filePath;

    public OpenFileManagerEvent(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getType() {
        return "OPEN_FILE_MANAGER";
    }

    public String getFilePath() {
        return filePath;
    }
}
