package ai.chat2db.community.web.api.model.request.data.source;

import lombok.Data;


@Data
public class SSHTestRequest {
    private boolean use;

    private String hostName;

    private String port;

    private String userName;

    private String localPort;

    private String authenticationType;

    private String password;

    private String keyFile;

    private String passphrase;
}
