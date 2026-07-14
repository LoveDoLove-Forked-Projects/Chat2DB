package ai.chat2db.community.tools.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginOrg {


    private Long id;


    private String name;


    private String type;


    private String status;


    private String token;


    private Long ownerId;


    private Long createUserId;


    private Long modifyUserId;


    private Date createTime;


    private Date modifyTime;


    private List<String> roleCodes;


    private boolean vip;


    private String organizationCode;


    private String organizationAvatar;
}
