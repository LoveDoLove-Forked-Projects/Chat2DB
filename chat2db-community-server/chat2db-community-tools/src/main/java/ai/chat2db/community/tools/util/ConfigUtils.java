package ai.chat2db.community.tools.util;

import ai.chat2db.community.tools.model.ConfigJson;
import ai.chat2db.community.tools.enums.NetworkStatus;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ai.chat2db.community.tools.enums.NetworkStatus.ONLINE;


@Slf4j
public class ConfigUtils {


    public static final String APP_PATH = getAppPath();

    private static String version = null;
    public static File versionFile = null;
    public static File configFile;
    private static ConfigJson config = null;

    public static File clientIdFile;
    private static String clientId = null;

    private static final String BASE_CHAT2DB_DIRECTORY = ".chat2db";

    private static final String BASE_CHAT2DB_LOCAL_DIRECTORY = ".chat2db_local_edition";

    private static final String BASE_CHAT2DB_COMMUNITY_DIRECTORY = ".chat2db-community";

    private static final String COMMUNITY_RUNTIME_MODE = "community";

    public static String getBasePath() {
        if (isCommunity()) {
            return System.getProperty("user.home") + File.separator + BASE_CHAT2DB_COMMUNITY_DIRECTORY;
        }
        if (isLocalEdition()) {
            return System.getProperty("user.home") + File.separator + BASE_CHAT2DB_LOCAL_DIRECTORY;
        } else {
            return System.getProperty("user.home") + File.separator + BASE_CHAT2DB_DIRECTORY;
        }
    }

    public static String getEnvBasePath(){
            return getBasePath() + File.separator + getEnv();
    }


    static {
        String environment = getEnv();
        if (APP_PATH != null) {
            versionFile = new File(
                    getAppPath() + File.separator + "versions" + File.separator + "version");
            if (!versionFile.exists()) {
                versionFile = null;
            }
        }
        configFile = new File(
                getBasePath() + File.separator + "config" + File.separator + "enterprise_config_" + environment + ".json");
        if (!configFile.exists()) {
            FileUtil.writeUtf8String(JSON.toJSONString(new ConfigJson()), configFile);
        }

        clientIdFile = new File(
                getBasePath() + File.separator + "config" + File.separator + "enterprise_client_uuid");
        if (!clientIdFile.exists()) {
            String uuid = UUID.fastUUID().toString(true);
            FileUtil.writeUtf8String(uuid, clientIdFile);
            clientId = uuid;
        }
    }

    public static String getEnv() {
        return StringUtils.defaultString(System.getProperty("spring.profiles.active"), "dev");
    }

    public static AppConfig getAppConfig() {
        return AppConfig.builder()
                .networkStatus(getNetworkStatus())
                .build();
    }

    public static boolean isOffline() {
        return isLocalEdition();
    }

    public static boolean isLocalPersistence() {
        return isCommunity() || isLocalEdition();
    }

    public static boolean isLocalEdition() {
        return !isCommunity() && isNetworkOffline();
    }

    public static boolean isNetworkOffline() {
        return NetworkStatus.OFFLINE.name().equalsIgnoreCase(getNetworkStatus());
    }

    public static String getRuntimeMode() {
        return StringUtils.defaultString(System.getProperty("chat2db.runtime.mode"), "pro");
    }

    public static boolean isCommunity() {
        return COMMUNITY_RUNTIME_MODE.equalsIgnoreCase(getRuntimeMode());
    }

    public static String getNetworkStatus() {
        return StringUtils.defaultString(
                System.getProperty("chat2db.network.status"),
                isCommunity() ? NetworkStatus.OFFLINE.name() : ONLINE.name());
    }

    public static void updateVersion(String version) {
        if (versionFile == null) {
            log.warn("VERSION_FILE is null");
            return;
        }
        FileUtil.writeUtf8String(version, versionFile);
        ConfigUtils.version = version;
    }

    public static String getLocalVersion() {
        if (versionFile == null) {
            log.warn("VERSION_FILE is null");
            return null;
        }
        if (version != null) {
            return version;
        }
        version = StringUtils.trim(FileUtil.readUtf8String(versionFile));
        return version;
    }

    public static String getLatestLocalVersion() {
        if (versionFile == null) {
            log.warn("VERSION_FILE is null");
            return null;
        }
        return StringUtils.trim(FileUtil.readUtf8String(versionFile));
    }


    public static ConfigJson getConfig() {
        if (config == null) {
            config = JSON.parseObject(StringUtils.trim(FileUtil.readUtf8String(configFile)), ConfigJson.class);
        }
        return config;
    }

    public static String getClientId() {
        if (clientId == null) {
            clientId = StringUtils.trim(FileUtil.readUtf8String(clientIdFile));
        }
        return clientId;
    }

    public static void setNetworkStatus(String status) {
        ConfigJson config = getConfig();
        config.setNetworkStatus(status);
        setConfig(config);
    }

    public static void setConfig(ConfigJson config) {
        String stringConfigJson = JSON.toJSONString(config);
        FileUtil.writeUtf8String(stringConfigJson, configFile);
        ConfigUtils.config = config;
        log.info("set config:{}", stringConfigJson);
    }

    private static String getAppPath() {
        try {
            String jarPath = System.getProperty("project.path");
            return FileUtil.getParent(jarPath, 4);
        } catch (Exception e) {
            log.error("getAppPath error", e);
            return null;
        }
    }

    public static boolean isDesktop() {
        String model = System.getProperty("chat2db.mode");
        return "DESKTOP".equalsIgnoreCase(model);
    }

    public static boolean isShowGUI() {
        String gui = System.getProperty("chat2db.gui");
        return !"false".equalsIgnoreCase(gui);
    }

    public static boolean isRelease() {
        String env = StringUtils.defaultString(System.getProperty("spring.profiles.active"), "dev");
        return env.contains("release");
    }


    private static Map<String, Object> CONFIG_MAP = new HashMap<>();


    public static String get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        if (CONFIG_MAP.isEmpty()) {
            loadConfig();
        }
        Object value = CONFIG_MAP.get(key);
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    private static synchronized void loadConfig() {
        if (!CONFIG_MAP.isEmpty()) {
            return;
        }
        Yaml yaml = new Yaml();
        Map<String, Object> result = new HashMap<>();
        Resource resource = new ClassPathResource("application.yml");
        try (InputStream in = resource.getInputStream()) {
            Map<String, Object> configMap = yaml.load(in);
            flattenMap("", configMap, result);
        } catch (IOException e) {
            log.error("load config error", e);
        }
        Resource resourceEnv = new ClassPathResource("application-" + getEnv() + ".yml");
        try (InputStream in = resourceEnv.getInputStream()) {
            Map<String, Object> envConfigMap = yaml.load(in);
            flattenMap("", envConfigMap, result);
        } catch (IOException e) {
            log.error("load env config error", e);
        }
        CONFIG_MAP = result;
    }

    private static void flattenMap(String prefix, Map<String, Object> source, Map<String, Object> target) {
        source.forEach((key, value) -> {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) value;
                flattenMap(fullKey, map, target);
            } else if (value instanceof List) {
                List<Object> list = (List<Object>) value;
                target.put(fullKey, list);
            } else {
                target.put(fullKey, value);
            }
        });
    }
}
