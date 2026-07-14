package ai.chat2db.community.domain.api.model.er;

import ai.chat2db.community.domain.api.model.metadata.Table;
import lombok.Data;

import java.util.List;

@Data
public class ERModel {


    private List<Table> tables;
    private String position;
}
