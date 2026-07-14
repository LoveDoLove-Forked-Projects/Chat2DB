package ai.chat2db.community.tools.console.bridge;

import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;

public interface IJcefServerBridge {

    void setHeaders(ConsoleMessage message);

    boolean isReady();

    ConsoleResult doController(ConsoleMessage message);

    ConsoleResult error(Exception e, ConsoleMessage message);

    boolean loginToken(String token);

    SqlExecuteResponse executeSql(ConsoleMessage message);

    boolean cancelSql(ConsoleMessage message);
}
