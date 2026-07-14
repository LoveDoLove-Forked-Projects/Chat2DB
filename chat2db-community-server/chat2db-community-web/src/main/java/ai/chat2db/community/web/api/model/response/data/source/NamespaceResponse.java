package ai.chat2db.community.web.api.model.response.data.source;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NamespaceResponse {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    private String name;


    private String description;


    private List<DataSourceResponse> dataSources;
}
