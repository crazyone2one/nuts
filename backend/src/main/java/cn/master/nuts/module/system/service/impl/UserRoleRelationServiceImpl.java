package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.UserRoleRelation;
import cn.master.nuts.module.system.mapper.UserRoleRelationMapper;
import cn.master.nuts.module.system.service.UserRoleRelationService;
import org.springframework.stereotype.Service;

/**
 * 用户组关系 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation>  implements UserRoleRelationService{

}
