package ai.chat2db.community.domain.api.service.sys;

import ai.chat2db.community.tools.network.NetworkProxySettings;

/**
 * Provides local network proxy settings.
 */
public interface ISysNetworkProxyService {

    NetworkProxySettings get();

    NetworkProxySettings save(NetworkProxySettings settings);

    boolean test(NetworkProxySettings settings, String testUrl) throws Exception;
}
