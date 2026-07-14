package ai.chat2db.community.tools.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Context {
    private LoginUser loginUser;
    private String organizationToken;
    private Long organizationId;
    private HeaderAndCookies headerAndCookies;
    private LoginOrg loginOrg;
    private String token;
}
