package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyViewRequest extends DataSourceBaseRequest {


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


    private String tempClause;


    private String algorithm;


    private String definer;


    private String security;


    private String viewName;


    private String viewBody;


    private String checkOption;

    private Boolean isModify;


}
