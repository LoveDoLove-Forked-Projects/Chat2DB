package ai.chat2db.spi.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageLimitRequest {

    @NotBlank
    private String sql;

    @PositiveOrZero
    private int offset;

    @Positive
    private int pageNo;

    @Positive
    private int pageSize;
}
