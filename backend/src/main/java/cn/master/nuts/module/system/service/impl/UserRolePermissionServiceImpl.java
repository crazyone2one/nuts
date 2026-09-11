package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.UserRolePermission;
import cn.master.nuts.module.system.mapper.UserRolePermissionMapper;
import cn.master.nuts.module.system.service.UserRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class UserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission>  implements UserRolePermissionService{

}
