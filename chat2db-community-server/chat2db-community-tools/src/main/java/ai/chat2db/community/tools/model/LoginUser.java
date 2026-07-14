package ai.chat2db.community.tools.model;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    private Long id;
    private String displayName;
    private Boolean admin;
    private List<String> roleCodes;
    private String email;
    private boolean vip;
    private boolean offline;
    private boolean activated;
    private String deviceId;
    private String country;
    private String language;
    private Date createTime;
    private String registerType;
}
