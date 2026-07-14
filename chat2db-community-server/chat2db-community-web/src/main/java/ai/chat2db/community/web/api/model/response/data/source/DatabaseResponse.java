package ai.chat2db.community.web.api.model.response.data.source;

import lombok.Data;


@Data
public class DatabaseResponse {


    private String name;


    private String description;


    private Integer count;


    private boolean system;
}
