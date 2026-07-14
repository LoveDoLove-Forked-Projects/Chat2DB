package ai.chat2db.community.jcef.handler.biz;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.context.JcefContext;
import ai.chat2db.community.jcef.menus.MenuI18n;
import ai.chat2db.community.jcef.utils.OSOperateUtil;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import org.apache.commons.lang3.StringUtils;
import org.cef.callback.CefQueryCallback;

import java.util.HashMap;
import java.util.Map;

@JcefAction(value = "select-sql-directory", method = "client-command")
public class SelectSqlDirectoryHandler implements IJcefActionHandler {
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        String directory = OSOperateUtil.openNativeDirChooser(
                JcefContext.getInstance().getFrame_(),
                MenuI18n.getString("fileChooser.select.dir.title")
        );
        if (StringUtils.isBlank(directory)) {
            Map<String, Object> result = new HashMap<>();
            result.put("data", null);
            ResponseBuilder.buildSuccessJcef(result, callback);
            return;
        }

        ResponseBuilder.buildSuccessJcef(Map.of("data", SqlDirectoryTreeStore.createRoot(directory)), callback);
    }
}
