package ai.chat2db.community.domain.api.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleIdentifier {
    private String name;
    private String alias;
    private String type;
    private String identifierDatabase;
    private String identifierSchema;
    private String identifierTable;
    private int identifierStartRowNum;
    private int identifierStartColNum;
    private int identifierEndColNum;
    private int identifierEndRowNum;
    private int aliasStartRowNum;
    private int aliasStartColNum;
    private int aliasEndColNum;
    private int aliasEndRowNum;
}
