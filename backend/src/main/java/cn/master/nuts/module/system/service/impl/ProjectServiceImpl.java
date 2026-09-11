package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.mapper.ProjectMapper;
import cn.master.nuts.module.system.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project>  implements ProjectService{

}
