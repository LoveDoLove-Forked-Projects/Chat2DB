package ai.chat2db.community.jcef.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
class UpdatePlan {
    @JsonProperty("tasks")
    private List<FileUpdateAction> tasks;

    @JsonProperty("downloadedFiles")
    private Map<String, String> downloadedFiles;

    @JsonProperty("remoteMetadata")
    private VersionMetadata remoteMetadata;

}
