package cn.master.nuts.module.system.service;

import cn.master.nuts.constants.HttpMethodConstants;
import cn.master.nuts.constants.OperationLogConstants;
import cn.master.nuts.dto.LogDTO;
import cn.master.nuts.dto.system.AddProjectRequest;
import cn.master.nuts.dto.system.UpdateProjectNameRequest;
import cn.master.nuts.dto.system.UpdateProjectRequest;
import cn.master.nuts.module.log.constants.OperationLogModule;
import cn.master.nuts.module.log.constants.OperationLogType;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.mapper.ProjectMapper;
import cn.master.nuts.util.JSON;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 11's papa
 * @since : 2026/9/11, 星期五
 **/
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SystemProjectLogService {
    private final ProjectMapper projectMapper;

    public LogDTO addLog(AddProjectRequest project) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                project.getName());

        dto.setOriginalValue(JSON.toJSONBytes(project));
        return dto;
    }

    public LogDTO updateLog(UpdateProjectRequest request) {
        Project project = projectMapper.selectOneById(request.getId());
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    request.getName());

            dto.setOriginalValue(JSON.toJSONBytes(project));
            return dto;
        }
        return null;
    }

    public LogDTO updateLog(String id) {
        Project project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    project.getName());
            dto.setMethod(HttpMethodConstants.GET.name());

            dto.setOriginalValue(JSON.toJSONBytes(project));
            return dto;
        }
        return null;
    }

    public LogDTO deleteLog(String id) {
        Project project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    id,
                    null,
                    OperationLogType.DELETE.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    project.getName());

            dto.setOriginalValue(JSON.toJSONBytes(project));
            return dto;
        }
        return null;
    }

    public LogDTO renameLog(UpdateProjectNameRequest request) {
        Project project = projectMapper.selectOneById(request.id());
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    request.name());

            dto.setOriginalValue(JSON.toJSONBytes(project));
            return dto;
        }
        return null;
    }
}
