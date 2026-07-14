package ai.chat2db.community.web.api.model.response.db;

import java.util.List;


import lombok.Data;


@Data
public class IndexResponse {


    private String columns;


    private String name;


    private String type;


    private String comment;


    private List<IndexColumnResponse> columnList;
}
