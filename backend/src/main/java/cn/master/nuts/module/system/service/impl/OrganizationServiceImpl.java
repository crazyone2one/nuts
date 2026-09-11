package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.Organization;
import cn.master.nuts.module.system.mapper.OrganizationMapper;
import cn.master.nuts.module.system.service.OrganizationService;
import org.springframework.stereotype.Service;

/**
 * 组织 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization>  implements OrganizationService{

}
