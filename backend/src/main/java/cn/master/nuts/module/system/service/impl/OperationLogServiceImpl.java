package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.OperationLog;
import cn.master.nuts.module.system.mapper.OperationLogMapper;
import cn.master.nuts.module.system.service.OperationLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>  implements OperationLogService{

}
