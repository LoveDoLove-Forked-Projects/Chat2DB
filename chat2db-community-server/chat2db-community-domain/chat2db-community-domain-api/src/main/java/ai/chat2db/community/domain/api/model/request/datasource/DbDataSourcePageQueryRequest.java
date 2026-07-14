package ai.chat2db.community.domain.api.model.request.datasource;

import ai.chat2db.community.tools.wrapper.param.OrderBy;
import ai.chat2db.community.tools.wrapper.param.PageQueryParam;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;


@Data
public class DbDataSourcePageQueryRequest extends PageQueryParam {


    @Size(max = 256)
    private String searchKey;

    private boolean refresh;


    private String kind;

    @Getter
    public enum OrderCondition implements ai.chat2db.community.tools.wrapper.param.IOrderCondition {
        ID_DESC(OrderBy.desc("id")),
        ;

        final OrderBy orderBy;

        OrderCondition(OrderBy orderBy) {
            this.orderBy = orderBy;
        }
    }
}
