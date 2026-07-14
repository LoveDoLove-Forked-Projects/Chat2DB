package ai.chat2db.community.jcef.handler.biz;


import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.utils.SystemSettingsUtil;
import ai.chat2db.community.tools.config.SystemSettingConstant;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.cef.callback.CefQueryCallback;

import java.util.Map;


@JcefAction(value = "update-settings", method = "client-command")
public class LangAndThemeHandler implements IJcefActionHandler {
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        JSONObject jsonObject = JSON.parseObject(consoleMessage.getMessage());
        String theme = jsonObject.getString(SystemSettingConstant.SYSTEM_APPEARANCE);
        String language = jsonObject.getString(SystemSettingConstant.SYSTEM_LANGUAGE);
        Boolean enableMcp = jsonObject.getBoolean(SystemSettingConstant.ENABLE_MCP);
        if (StringUtils.isNotBlank(language)) {
            SystemSettingsUtil.changeLanguage(language);
        }
        if (StringUtils.isNotBlank(theme)) {
            SystemSettingsUtil.changeTheme(theme);
        }
        if (enableMcp != null) {
            SystemSettingsUtil.setProperty(SystemSettingConstant.ENABLE_MCP, enableMcp);
        }
        ResponseBuilder.buildSuccessJcef(Map.of("data", true), callback);
    }
}
