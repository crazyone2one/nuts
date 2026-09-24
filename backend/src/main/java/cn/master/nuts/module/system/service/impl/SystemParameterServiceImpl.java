package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.SystemParameter;
import cn.master.nuts.module.system.mapper.SystemParameterMapper;
import cn.master.nuts.module.system.service.SystemParameterService;
import org.springframework.stereotype.Service;

/**
 * 系统参数 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-22
 */
@Service
public class SystemParameterServiceImpl extends ServiceImpl<SystemParameterMapper, SystemParameter>  implements SystemParameterService{

}
