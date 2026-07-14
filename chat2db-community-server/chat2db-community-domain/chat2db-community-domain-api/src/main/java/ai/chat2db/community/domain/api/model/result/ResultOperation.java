package ai.chat2db.community.domain.api.model.result;

import lombok.Data;
import java.util.List;

@Data
public class ResultOperation {

    private String type;

    private List<String> dataList;

    private List<String> oldDataList;
    private List<Integer> selectCols;
    private ResultCell selectedCell;
}
