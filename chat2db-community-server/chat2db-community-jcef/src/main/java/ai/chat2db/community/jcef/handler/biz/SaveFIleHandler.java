package ai.chat2db.community.jcef.handler.biz;


import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.context.JcefContext;
import ai.chat2db.community.jcef.menus.MenuI18n;
import ai.chat2db.community.jcef.utils.OSOperateUtil;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.cef.callback.CefQueryCallback;

import java.util.HashMap;
import java.util.Map;


@JcefAction(value = "save-file", method = "client-command")
public class SaveFIleHandler implements IJcefActionHandler {
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        JSONObject jsonObject = JSON.parseObject(consoleMessage.getMessage());
        String fileName = jsonObject.getString("fileName");
        String fileContent = jsonObject.getString("fileContent");
        String fileType = jsonObject.getString("fileType");
        String defaultFileName = normalizeDefaultFileName(fileName, fileType);
        String filePath = OSOperateUtil.openNativeSaveFileChooser(
                JcefContext.getInstance().getFrame_(),
                MenuI18n.getString("fileChooser.select.file.title"),
                defaultFileName
        );
        if (filePath == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", null);
            ResponseBuilder.buildSuccessJcef(response, callback);
            return;
        }

        Map<String, Object> result = OSOperateUtil.saveFile(filePath, fileContent, fileType);
        ResponseBuilder.buildSuccessJcef(Map.of("data", result), callback);
    }

    private String normalizeDefaultFileName(String fileName, String fileType) {
        String normalizedFileName = fileName == null || fileName.isBlank() ? "untitled" : fileName.trim();
        String normalizedFileType = fileType == null || fileType.isBlank() ? "sql" : fileType.trim();
        if (normalizedFileName.toLowerCase().endsWith("." + normalizedFileType.toLowerCase())) {
            return normalizedFileName;
        }
        return normalizedFileName + "." + normalizedFileType;
    }
}
