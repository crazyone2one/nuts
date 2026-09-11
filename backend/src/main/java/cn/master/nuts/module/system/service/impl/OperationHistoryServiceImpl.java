package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.OperationHistory;
import cn.master.nuts.module.system.mapper.OperationHistoryMapper;
import cn.master.nuts.module.system.service.OperationHistoryService;
import org.springframework.stereotype.Service;

/**
 * 变更记录 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class OperationHistoryServiceImpl extends ServiceImpl<OperationHistoryMapper, OperationHistory>  implements OperationHistoryService{

}
