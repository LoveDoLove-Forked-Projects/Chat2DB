package ai.chat2db.community.tools.wrapper.request;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;
@Data
@SuperBuilder
@AllArgsConstructor
public class PageQueryRequest {
    @NotNull(message = "Page number is required")
    private Integer pageNo;
    @NotNull(message = "Page size is required")
    @Range(min = 1, max = 100000,
        message = "Page size must be between 1 and " + 100000 + ".")
    private Integer pageSize;
    public PageQueryRequest() {
        this.pageNo = 1;
        this.pageSize = 10;
    }
}
