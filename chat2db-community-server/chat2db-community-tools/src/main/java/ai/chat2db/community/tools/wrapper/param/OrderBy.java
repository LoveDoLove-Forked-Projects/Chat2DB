package ai.chat2db.community.tools.wrapper.param;


import ai.chat2db.community.tools.enums.OrderByDirectionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBy {


    private String orderConditionName;


    private OrderByDirectionEnum direction;

    public static OrderBy of(String property, OrderByDirectionEnum direction) {
        return new OrderBy(property, direction);
    }

    public static OrderBy asc(String property) {
        return new OrderBy(property, OrderByDirectionEnum.ASC);
    }

    public static OrderBy desc(String property) {
        return new OrderBy(property, OrderByDirectionEnum.DESC);
    }
}
