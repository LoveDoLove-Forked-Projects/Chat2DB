package ai.chat2db.community.tools.network;

import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.community.tools.enums.network.NetworkProxyModeEnum;
import ai.chat2db.community.tools.enums.network.NetworkProxyTypeEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NetworkProxyUtilTest {

    @AfterEach
    void tearDown() {
        NetworkProxySettings settings = new NetworkProxySettings();
        settings.setMode(NetworkProxyModeEnum.NO_PROXY);
        NetworkProxyUtil.applyToJvm(settings);
    }

    @Test
    void shouldApplyManualHttpProxyToJvmProperties() {
        NetworkProxySettings settings = new NetworkProxySettings();
        settings.setMode(NetworkProxyModeEnum.MANUAL);
        settings.setProxyType(NetworkProxyTypeEnum.HTTP);
        settings.setHost("127.0.0.1");
        settings.setPort(7897);
        settings.setNoProxyHosts("localhost, 127.*\n[::1]");

        NetworkProxyUtil.applyToJvm(settings);

        assertEquals("127.0.0.1", System.getProperty("http.proxyHost"));
        assertEquals("7897", System.getProperty("http.proxyPort"));
        assertEquals("127.0.0.1", System.getProperty("https.proxyHost"));
        assertEquals("7897", System.getProperty("https.proxyPort"));
        assertEquals("localhost|127.*|[::1]", System.getProperty("http.nonProxyHosts"));
    }

    @Test
    void shouldApplyManualSocksProxyToJvmProperties() {
        NetworkProxySettings settings = new NetworkProxySettings();
        settings.setMode(NetworkProxyModeEnum.MANUAL);
        settings.setProxyType(NetworkProxyTypeEnum.SOCKS);
        settings.setHost("127.0.0.1");
        settings.setPort(7897);

        NetworkProxyUtil.applyToJvm(settings);

        assertEquals("127.0.0.1", System.getProperty("socksProxyHost"));
        assertEquals("7897", System.getProperty("socksProxyPort"));
    }

    @Test
    void shouldBuildChromiumProxySwitchValues() {
        NetworkProxySettings settings = new NetworkProxySettings();
        settings.setMode(NetworkProxyModeEnum.MANUAL);
        settings.setProxyType(NetworkProxyTypeEnum.HTTP);
        settings.setHost("proxy.example.com");
        settings.setPort(8080);

        assertEquals("http=proxy.example.com:8080;https=proxy.example.com:8080",
                NetworkProxyUtil.toChromiumProxyServer(settings));
        assertEquals("localhost;127.*;[::1]", NetworkProxyUtil.toChromiumBypassList("localhost|127.*|[::1]"));
    }

    @Test
    void shouldRejectManualProxyWithoutValidPort() {
        NetworkProxySettings settings = new NetworkProxySettings();
        settings.setMode(NetworkProxyModeEnum.MANUAL);
        settings.setProxyType(NetworkProxyTypeEnum.HTTP);
        settings.setHost("127.0.0.1");
        settings.setPort(70000);

        assertThrows(BusinessException.class, () -> NetworkProxyUtil.validate(settings));
    }
}
