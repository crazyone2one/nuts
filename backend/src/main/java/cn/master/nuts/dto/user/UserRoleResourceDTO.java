package cn.master.nuts.dto.user;

import cn.master.nuts.handler.result.Views;
import cn.master.nuts.module.system.entity.UserRole;
import cn.master.nuts.module.system.entity.UserRolePermission;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/14, 星期一
 **/
@Data
public class UserRoleResourceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonView(Views.Internal.class)
    private UserRoleResource resource;
    @JsonView(Views.Internal.class)
    private List<UserRolePermission> permissions;
    @JsonView(Views.Internal.class)
    private String type;
    @JsonView(Views.Internal.class)
    private UserRole userRole;
    @JsonView(Views.Internal.class)
    private List<UserRolePermission> userRolePermissions;
}
