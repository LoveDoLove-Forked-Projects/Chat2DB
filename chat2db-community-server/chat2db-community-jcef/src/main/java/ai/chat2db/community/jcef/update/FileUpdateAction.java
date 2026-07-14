package ai.chat2db.community.jcef.update;

import ai.chat2db.community.jcef.enums.update.UpdateActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUpdateAction {
    public FileInfo remoteFileInfo;
    public FileInfo localFileInfo;
    public UpdateActionType actionType;
    public String reason;

    FileUpdateAction(UpdateActionType actionType, FileInfo remoteFileInfo, FileInfo localFileInfo, String reason) {
        this.actionType = actionType;
        this.remoteFileInfo = remoteFileInfo;
        this.localFileInfo = localFileInfo;
        this.reason = reason;
    }

    @Override
    public String toString() {
        String fileInfoStr = remoteFileInfo != null ? remoteFileInfo.localTargetName : (localFileInfo != null ? localFileInfo.localTargetName : "N/A");
        return actionType + " for " + fileInfoStr + " (" + reason + ")";
    }
}
