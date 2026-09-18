package cn.master.nuts.module.system.service.impl;

import cn.master.nuts.dto.LogDTO;
import cn.master.nuts.module.system.entity.OperationHistory;
import cn.master.nuts.module.system.entity.OperationLog;
import cn.master.nuts.module.system.mapper.OperationHistoryMapper;
import cn.master.nuts.module.system.mapper.OperationLogMapper;
import cn.master.nuts.module.system.service.OperationLogService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    private final OperationHistoryMapper operationHistoryMapper;

    @Override
    public void add(LogDTO log) {
        if (StringUtils.isBlank(log.getProjectId())) {
            log.setProjectId("none");
        }
        if (StringUtils.isBlank(log.getCreateUser())) {
            log.setCreateUser("admin");
        }
        log.setContent(subStrContent(log.getContent()));
        mapper.insertSelective(log);
        if (log.getHistory()) {
            operationHistoryMapper.insert(getHistory(log));
        }
    }

    @Override
    public void batchAdd(List<LogDTO> logs) {
        logs.forEach(this::add);
    }

    private static OperationHistory getHistory(LogDTO log) {
        OperationHistory history = new OperationHistory();
        BeanUtils.copyProperties(log, history);
        return history;
    }

    private String subStrContent(String content) {
        if (StringUtils.isNotBlank(content) && content.length() > 500) {
            return content.substring(0, 499);
        }
        return content;
    }

}
