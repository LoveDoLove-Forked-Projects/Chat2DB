package ai.chat2db.community.tools.exception.cli;

import java.util.Map;

import lombok.Getter;

@Getter
public class CliDomainException extends RuntimeException {

    private final String code;

    private final Map<String, Object> details;

    public CliDomainException(String code, String message) {
        super(message);
        this.code = code;
        this.details = Map.of();
    }

    public CliDomainException(String code, String message, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.details = details == null ? Map.of() : details;
    }
}
