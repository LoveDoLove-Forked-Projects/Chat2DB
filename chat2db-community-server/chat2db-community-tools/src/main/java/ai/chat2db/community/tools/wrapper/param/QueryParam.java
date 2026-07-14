package ai.chat2db.community.tools.wrapper.param;
import ai.chat2db.community.tools.enums.OrderByDirectionEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QueryParam {
    private List<OrderBy> orderByList;
    public QueryParam orderBy(OrderBy orderBy) {
        orderByList = new ArrayList<>();
        orderByList.add(orderBy);
        return this;
    }
    public QueryParam orderBy(String orderConditionName, OrderByDirectionEnum direction) {
        return orderBy(new OrderBy(orderConditionName, direction));
    }
    public QueryParam orderBy(IOrderCondition orderCondition) {
        return orderBy(orderCondition.getOrderBy());
    }
    public QueryParam andOrderBy(OrderBy orderBy) {
        orderByList.add(orderBy);
        return this;
    }
    public QueryParam andOrderBy(String orderConditionName, OrderByDirectionEnum direction) {
        return andOrderBy(new OrderBy(orderConditionName, direction));
    }
    public QueryParam andOrderBy(IOrderCondition orderCondition) {
        return andOrderBy(orderCondition.getOrderBy());
    }
}
