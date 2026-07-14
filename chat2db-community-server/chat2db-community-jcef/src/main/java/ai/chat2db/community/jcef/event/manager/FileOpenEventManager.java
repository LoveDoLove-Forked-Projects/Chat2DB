package ai.chat2db.community.jcef.event.manager;

import ai.chat2db.community.jcef.context.JcefContext;
import ai.chat2db.community.jcef.enums.ActionTypeEnum;
import ai.chat2db.community.jcef.utils.CallJsFunctionUtil;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
public class FileOpenEventManager {
    private static final ConcurrentLinkedQueue<String> pendingFilePaths = new ConcurrentLinkedQueue<>();
    private static volatile boolean isAppReady = false;
    public static void stashFileOpenEvent(String filePath) {
        pendingFilePaths.add(filePath);
        if (isAppReady) {
            processPendingEvents();
        }
    }
    public static void markAppReady() {
        isAppReady = true;
        processPendingEvents();
    }
    private static void processPendingEvents() {
        while (!pendingFilePaths.isEmpty()) {
            String filePath = pendingFilePaths.poll();
            handleFileOpen(filePath);
        }
    }

    public static void handleFileOpen(String openFilePath) {
        log.info("Opened file {}", openFilePath);
        ConsoleResult consoleResult = new ConsoleResult();
        consoleResult.setMessage(Map.of("data", openFilePath));
        consoleResult.setActionType(ActionTypeEnum.OPEN_FILE.getName());
        String result = JSON.toJSONString(consoleResult);
        JcefContext.getInstance().getFrame_().setVisible(true);
        if (JcefContext.getInstance().getFrame_().getExtendedState() == Frame.ICONIFIED) {
            JcefContext.getInstance().getFrame_().setExtendedState(Frame.NORMAL);
        }
        CallJsFunctionUtil.callHandleJavaMessage(JcefContext.getInstance().getBrowser_(), result);
    }
}
