package ai.chat2db.community.tools.network;

import com.fasterxml.jackson.databind.ObjectMapper;

final class NetworkProxySettingsConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private NetworkProxySettingsConverter() {
    }

    static NetworkProxySettings toSettings(Object value) {
        return OBJECT_MAPPER.convertValue(value, NetworkProxySettings.class);
    }
}
