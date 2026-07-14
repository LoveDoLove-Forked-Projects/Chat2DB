package ai.chat2db.community.domain.api.config;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Environment {
    private Long id;
    private String name;
    private String shortName;
    private String color;
}
