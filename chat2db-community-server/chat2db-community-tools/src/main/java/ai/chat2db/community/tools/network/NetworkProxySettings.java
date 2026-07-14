package ai.chat2db.community.tools.network;

import ai.chat2db.community.tools.enums.network.NetworkProxyModeEnum;
import ai.chat2db.community.tools.enums.network.NetworkProxyTypeEnum;
import lombok.Data;

@Data
public class NetworkProxySettings {

    private NetworkProxyModeEnum mode = NetworkProxyModeEnum.NO_PROXY;

    private NetworkProxyTypeEnum proxyType = NetworkProxyTypeEnum.HTTP;

    private String host = "";

    private Integer port;

    private String noProxyHosts = "localhost|127.*|[::1]";

    private boolean restartRequired;
}
