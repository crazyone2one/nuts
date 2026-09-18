package cn.master.nuts.module.system.service;

import cn.master.nuts.dto.LogDTO;
import com.mybatisflex.core.service.IService;
import cn.master.nuts.module.system.entity.OperationLog;

import java.util.List;

/**
 * 操作日志 服务层。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
public interface OperationLogService extends IService<OperationLog> {
    void add(LogDTO log);

    void batchAdd(List<LogDTO> logs);
}
