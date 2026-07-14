package ai.chat2db.community.web.api.model.request.system;

import ai.chat2db.community.tools.network.NetworkProxySettings;
import lombok.Data;

@Data
public class NetworkProxyTestRequest {

    private NetworkProxySettings settings;

    private String testUrl;
}
