package cn.master.nuts.module.project.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.project.entity.EnvironmentGroup;
import cn.master.nuts.module.project.mapper.EnvironmentGroupMapper;
import cn.master.nuts.module.project.service.EnvironmentGroupService;
import org.springframework.stereotype.Service;

/**
 * 环境组 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@Service
public class EnvironmentGroupServiceImpl extends ServiceImpl<EnvironmentGroupMapper, EnvironmentGroup>  implements EnvironmentGroupService{

}
