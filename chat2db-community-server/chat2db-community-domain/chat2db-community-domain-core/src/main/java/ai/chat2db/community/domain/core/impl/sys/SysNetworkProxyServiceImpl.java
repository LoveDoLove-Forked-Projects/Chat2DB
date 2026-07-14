package ai.chat2db.community.domain.core.impl.sys;

import ai.chat2db.community.domain.api.service.sys.ISysNetworkProxyService;
import ai.chat2db.community.tools.network.NetworkProxySettings;
import ai.chat2db.community.tools.network.NetworkProxyUtil;
import org.springframework.stereotype.Service;

@Service
public class SysNetworkProxyServiceImpl implements ISysNetworkProxyService {

    @Override
    public NetworkProxySettings get() {
        return NetworkProxyUtil.getSettings();
    }

    @Override
    public NetworkProxySettings save(NetworkProxySettings settings) {
        return NetworkProxyUtil.saveSettings(settings);
    }

    @Override
    public boolean test(NetworkProxySettings settings, String testUrl) throws Exception {
        NetworkProxyUtil.checkConnection(settings, testUrl);
        return true;
    }
}
