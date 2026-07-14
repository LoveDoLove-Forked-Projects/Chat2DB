package ai.chat2db.community.web.api.model.request.mcp;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McpExecuteSqlRequest {


    @NotBlank
    @ToolParam(description = "SQL statement to execute")
    private String sql;


    @ToolParam(description = "Page number")
    private Integer pageNo;


    @ToolParam(description = "Page size")
    private Integer pageSize;


}
