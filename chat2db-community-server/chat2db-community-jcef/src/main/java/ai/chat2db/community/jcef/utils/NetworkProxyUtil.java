package ai.chat2db.community.jcef.utils;

import ai.chat2db.community.tools.network.NetworkProxySettings;
import ai.chat2db.community.tools.enums.network.NetworkProxyModeEnum;
import org.apache.commons.lang3.StringUtils;
import org.cef.callback.CefCommandLine;

public class NetworkProxyUtil {

    private NetworkProxyUtil() {
    }

    public static void applyToCefCommandLine(CefCommandLine commandLine) {
        if (commandLine == null) {
            return;
        }
        NetworkProxySettings settings = ai.chat2db.community.tools.network.NetworkProxyUtil.getSettings();
        if (settings.getMode() == NetworkProxyModeEnum.NO_PROXY) {
            commandLine.appendSwitch("no-proxy-server");
            return;
        }
        if (settings.getMode() != NetworkProxyModeEnum.MANUAL) {
            return;
        }
        ai.chat2db.community.tools.network.NetworkProxyUtil.validate(settings);
        commandLine.appendSwitchWithValue("proxy-server",
                ai.chat2db.community.tools.network.NetworkProxyUtil.toChromiumProxyServer(settings));
        String bypassList = ai.chat2db.community.tools.network.NetworkProxyUtil.toChromiumBypassList(settings.getNoProxyHosts());
        if (StringUtils.isNotBlank(bypassList)) {
            commandLine.appendSwitchWithValue("proxy-bypass-list", bypassList);
        }
    }
}
