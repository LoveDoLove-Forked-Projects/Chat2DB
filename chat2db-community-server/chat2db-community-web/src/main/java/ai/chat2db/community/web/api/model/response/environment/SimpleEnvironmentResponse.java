package ai.chat2db.community.web.api.model.response.environment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleEnvironmentResponse {
    private Long id;
    private String name;
    private String shortName;
    private String color;
}
