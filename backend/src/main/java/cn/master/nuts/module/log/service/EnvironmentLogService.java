package cn.master.nuts.module.log.service;

import cn.master.nuts.dto.LogDTO;
import cn.master.nuts.dto.environment.EnvironmentConfig;
import cn.master.nuts.dto.environment.EnvironmentRequest;
import cn.master.nuts.module.log.constants.OperationLogModule;
import cn.master.nuts.module.log.constants.OperationLogType;
import cn.master.nuts.module.project.entity.Environment;
import cn.master.nuts.module.project.mapper.EnvironmentMapper;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.mapper.ProjectMapper;
import cn.master.nuts.util.JSON;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class EnvironmentLogService {
    private final ProjectMapper projectMapper;
    private final EnvironmentMapper environmentMapper;

    public LogDTO addLog(EnvironmentRequest request) {
        Project project = getProject(request.getProjectId());
        LogDTO dto = new LogDTO(
                request.getProjectId(),
                project.getOrganizationId(),
                request.getId(),
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.PROJECT_MANAGEMENT_ENVIRONMENT,
                request.getName());
        dto.setOriginalValue(JSON.toJSONBytes(request));
        return dto;
    }

    public LogDTO updateLog(EnvironmentRequest request) {
        Project project = getProject(request.getProjectId());
        LogDTO dto = new LogDTO(
                project.getId(),
                project.getOrganizationId(),
                request.getId(),
                null,
                OperationLogType.UPDATE.name(),
                OperationLogModule.PROJECT_MANAGEMENT_ENVIRONMENT,
                request.getName());
        Environment environment = environmentMapper.selectOneById(request.getId());
        EnvironmentRequest before = new EnvironmentRequest();
        before.setName(environment.getName());
        before.setConfig(JSON.parseObject(new String(environment.getConfig()), EnvironmentConfig.class));
        dto.setOriginalValue(JSON.toJSONBytes(before));
        dto.setModifiedValue(JSON.toJSONBytes(request));
        return dto;
    }

    public LogDTO deleteLog(String id) {
        Environment environment = environmentMapper.selectOneById(id);
        Project project = getProject(environment.getProjectId());
        return new LogDTO(
                project.getId(),
                project.getOrganizationId(),
                id,
                null,
                OperationLogType.DELETE.name(),
                OperationLogModule.PROJECT_MANAGEMENT_ENVIRONMENT,
                environment.getName());
    }

    private Project getProject(String id) {
        return projectMapper.selectOneById(id);
    }
}
