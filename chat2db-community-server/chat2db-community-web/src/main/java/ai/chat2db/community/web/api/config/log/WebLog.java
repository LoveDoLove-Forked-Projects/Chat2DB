package ai.chat2db.community.web.api.config.log;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WebLog {


    private String method;


    private String path;


    private String query;


    private Long duration;


    private LocalDateTime startTime;


    private LocalDateTime endTime;


    private String request;


    private String response;


    private String ip;
}
