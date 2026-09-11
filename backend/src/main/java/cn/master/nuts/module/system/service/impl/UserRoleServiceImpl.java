package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.UserRole;
import cn.master.nuts.module.system.mapper.UserRoleMapper;
import cn.master.nuts.module.system.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>  implements UserRoleService{

}
