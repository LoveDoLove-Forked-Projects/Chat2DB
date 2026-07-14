package ai.chat2db.community.domain.api.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleInsertValueMapping {

    private Integer columnStartRowNum;
    private Integer columnStartColNum;
    private Integer columnEndRowNum;
    private Integer columnEndColNum;
    private Integer valueStartRowNum;
    private Integer valueStartColNum;
    private Integer valueEndRowNum;
    private Integer valueEndColNum;
    private Integer rowStartRowNum;
    private Integer rowStartColNum;
    private Integer rowEndRowNum;
    private Integer rowEndColNum;
    private int rowIndex;
    private int columnIndex;
    private String mappingStatus;
}
