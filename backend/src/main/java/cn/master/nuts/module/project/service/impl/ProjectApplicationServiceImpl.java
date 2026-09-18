package cn.master.nuts.module.project.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.project.entity.ProjectApplication;
import cn.master.nuts.module.project.mapper.ProjectApplicationMapper;
import cn.master.nuts.module.project.service.ProjectApplicationService;
import org.springframework.stereotype.Service;

/**
 * 项目应用 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@Service
public class ProjectApplicationServiceImpl extends ServiceImpl<ProjectApplicationMapper, ProjectApplication>  implements ProjectApplicationService{

}
