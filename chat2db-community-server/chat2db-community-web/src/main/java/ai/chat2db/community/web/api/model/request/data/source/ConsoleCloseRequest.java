package ai.chat2db.community.web.api.model.request.data.source;

import lombok.Data;


@Data
public class ConsoleCloseRequest extends DataSourceBaseRequest implements IDataSourceConsoleRequestInfo{


    private Long consoleId;
}
