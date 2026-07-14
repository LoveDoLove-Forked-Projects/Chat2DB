package ai.chat2db.community.domain.api.model.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyView {




    private boolean useOrReplace;




    private boolean useForce;




    private boolean useIfNotExists;




    private boolean useRecursive;




    private boolean useOrAlter;




    private List<String> viewAttributes;




    private String editClause;




    private String shareClause;




    private String collationClause;




    private String containerClause;




    private String subqueryRestrictionClause;




    private String subqueryConstraintName;




    private String storageClause;




    private String algorithm;




    private String definer;




    private String security;




    private String viewName;




    private String viewBody;




    private String checkOption;




    private String databaseName;




    private String schemaName;




    private String comment;

    private Boolean isModify;

}
