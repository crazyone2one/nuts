package cn.master.nuts.dto.system;

import cn.master.nuts.dto.user.UserRoleResourceDTO;
import cn.master.nuts.handler.result.Views;
import cn.master.nuts.module.system.entity.User;
import cn.master.nuts.module.system.entity.UserRole;
import cn.master.nuts.module.system.entity.UserRoleRelation;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/3, 星期四
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends User {

    @JsonView(Views.Internal.class)
    private String accessToken;
    @JsonView(Views.Internal.class)
    private List<UserRole> userRoles = new ArrayList<>();
    @JsonView(Views.Internal.class)
    private List<UserRoleRelation> userRoleRelations = new ArrayList<>();
    @JsonView(Views.Internal.class)
    private List<UserRoleResourceDTO> userRolePermissions = new ArrayList<>();
}
