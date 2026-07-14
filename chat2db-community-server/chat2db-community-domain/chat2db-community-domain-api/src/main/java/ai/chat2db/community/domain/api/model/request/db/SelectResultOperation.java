package ai.chat2db.community.domain.api.model.request.db;

import ai.chat2db.community.domain.api.model.result.ResultCell;
import lombok.Data;

import java.util.List;

@Data
public class SelectResultOperation {

    private String type;

    private List<String> dataList;

    private List<String> oldDataList;
    private List<Integer> selectCols;
    private ResultCell selectedCell;
}
