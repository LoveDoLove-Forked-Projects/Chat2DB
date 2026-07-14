package ai.chat2db.community.domain.api.model.cli;

import java.util.List;

import lombok.Data;

@Data
public class CliRuntimeCapabilities {

    private List<String> features;
}
