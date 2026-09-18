package cn.master.nuts.module.project.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.project.entity.ProjectParameter;
import cn.master.nuts.module.project.mapper.ProjectParameterMapper;
import cn.master.nuts.module.project.service.ProjectParameterService;
import org.springframework.stereotype.Service;

/**
 * 项目级参数 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@Service
public class ProjectParameterServiceImpl extends ServiceImpl<ProjectParameterMapper, ProjectParameter>  implements ProjectParameterService{

}
