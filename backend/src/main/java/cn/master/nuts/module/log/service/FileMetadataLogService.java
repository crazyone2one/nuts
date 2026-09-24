package cn.master.nuts.module.log.service;

import cn.master.nuts.constants.HttpMethodConstants;
import cn.master.nuts.dto.LogDTO;
import cn.master.nuts.dto.LogDTOBuilder;
import cn.master.nuts.module.log.constants.OperationLogModule;
import cn.master.nuts.module.log.constants.OperationLogType;
import cn.master.nuts.module.project.entity.FileMetadata;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.mapper.ProjectMapper;
import cn.master.nuts.module.system.service.OperationLogService;
import cn.master.nuts.util.JSON;
import cn.master.nuts.util.Translator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class FileMetadataLogService {
    private String logModule = OperationLogModule.PROJECT_FILE_MANAGEMENT;
    private final ProjectMapper projectMapper;
    private final OperationLogService operationLogService;

    public void saveUploadLog(FileMetadata module, String operator) {
        Project project = projectMapper.selectOneById(module.getProjectId());
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(module.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.ADD.name())
                .module(logModule)
                .method(HttpMethodConstants.POST.name())
                .path("/project/file/upload")
                .sourceId(module.getId())
                .content(Translator.get("file.log.upload") + " " + module.getName())
                .originalValue(JSON.toJSONBytes(module))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    public void saveDeleteLog(List<FileMetadata> deleteList, String projectId, String operator) {
        Project project = projectMapper.selectOneById(projectId);
        List<LogDTO> list = new ArrayList<>();
        for (FileMetadata fileMetadata : deleteList) {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(projectId)
                    .organizationId(project.getOrganizationId())
                    .type(OperationLogType.DELETE.name())
                    .module(logModule)
                    .method(HttpMethodConstants.POST.name())
                    .path("/project/file/delete")
                    .sourceId(fileMetadata.getId())
                    .content(fileMetadata.getName())
                    .originalValue(JSON.toJSONBytes(fileMetadata))
                    .createUser(operator)
                    .build().getLogDTO();
            list.add(dto);
        }

        operationLogService.batchAdd(list);
    }
}
