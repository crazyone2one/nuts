package cn.master.nuts.module.project.service;

import cn.master.nuts.constants.DefaultRepositoryDir;
import cn.master.nuts.constants.ModuleConstants;
import cn.master.nuts.dto.filemanagement.FileBatchProcessRequest;
import cn.master.nuts.dto.filemanagement.FileRequest;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.module.log.service.FileMetadataLogService;
import cn.master.nuts.module.log.service.FileModuleLogService;
import cn.master.nuts.module.project.entity.FileMetadata;
import cn.master.nuts.module.project.entity.FileModule;
import cn.master.nuts.module.project.mapper.FileMetadataMapper;
import cn.master.nuts.module.system.service.FileService;
import cn.master.nuts.util.TempFileUtils;
import cn.master.nuts.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.master.nuts.module.project.entity.table.FileMetadataTableDef.FILE_METADATA;
import static cn.master.nuts.module.project.entity.table.FileModuleTableDef.FILE_MODULE;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final FileMetadataMapper fileMetadataMapper;
    private final FileService fileService;
    private final FileMetadataLogService fileMetadataLogService;


    public void checkModule(String moduleId, String nodeTypeDefault) {
        if (!Strings.CS.equals(moduleId, ModuleConstants.DEFAULT_NODE_ID)) {
            boolean exists = QueryChain.of(FileModule.class).where(FILE_MODULE.ID.eq(moduleId)
                    .and(FILE_MODULE.MODULE_TYPE.eq(nodeTypeDefault))).exists();
            if (!exists) {
                throw new NSException("file_module.not.exist");
            }
        }
    }

    public void deleteByModuleIds(List<String> deleteIds) {
        List<FileMetadata> refFileList = QueryChain.of(FileMetadata.class).where(FILE_METADATA.MODULE_ID.in(deleteIds)).list();
        List<String> refIdList = refFileList.stream().map(FileMetadata::getRefId).toList();
        if (CollectionUtils.isNotEmpty(refIdList)) {
            List<FileMetadata> deleteList = QueryChain.of(FileMetadata.class).where(FILE_METADATA.REF_ID.in(refIdList)).list();
            if (CollectionUtils.isNotEmpty(deleteList)) {
                fileMetadataMapper.deleteBatchByIds(deleteList.stream().map(FileMetadata::getId).toList());
                deleteList.forEach(fileMetadata -> {
                    FileRequest fileRequest = new FileRequest();
                    fileRequest.setFileName(fileMetadata.getId());
                    fileRequest.setStorage(fileMetadata.getStorage());
                    try {
                        fileService.deleteFile(fileRequest);
                    } catch (Exception e) {
                        log.error("删除文件失败", e);
                    }
                });
            }
        }
    }

    public byte[] getFile(FileMetadata fileMetadata) throws Exception {
        if (fileMetadata == null) {
            throw new NSException(Translator.get("file.not.exist"));
        }

        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileName(fileMetadata.getId());
        fileRequest.setFolder(DefaultRepositoryDir.getFileManagementDir(fileMetadata.getProjectId()));
        fileRequest.setStorage(fileMetadata.getStorage());

        return fileService.download(fileRequest);
    }

    public void delete(FileBatchProcessRequest request, String operator) {
        List<FileMetadata> deleteList = getDeleteList(request);
        List<String> deleteIds = deleteList.stream().map(FileMetadata::getId).toList();
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            fileMetadataMapper.deleteByQuery(QueryChain.of(FileMetadata.class).where(FILE_METADATA.ID.in(deleteIds)));
            fileMetadataLogService.saveDeleteLog(deleteList, request.getProjectId(), operator);
            deleteList.forEach(fileMetadata -> {
                FileRequest fileRequest = new FileRequest();
                fileRequest.setFileName(fileMetadata.getId());
                fileRequest.setStorage(fileMetadata.getStorage());
                fileRequest.setFolder(DefaultRepositoryDir.getFileManagementDir(fileMetadata.getProjectId()));
                //删除临时文件
                TempFileUtils.deleteTmpFile(fileMetadata.getId());
                try {
                    //删除存储容器中的文件
                    fileService.deleteFile(fileRequest);
                    //删除缓存文件
                    fileRequest.setFolder(DefaultRepositoryDir.getFileManagementPreviewDir(fileMetadata.getProjectId()));
                    fileService.deleteFile(fileRequest);
                } catch (Exception e) {
                    log.error("删除文件失败", e);
                }
            });
        }
    }

    private List<FileMetadata> getDeleteList(FileBatchProcessRequest request) {
        List<String> processIds = request.getSelectIds();
        List<FileMetadata> refFileList = new ArrayList<>();
        List<String> refIdList = QueryChain.of(FileMetadata.class).select(FILE_METADATA.REF_ID).where(FILE_METADATA.REF_ID.in(processIds)).listAs(String.class);
        if (CollectionUtils.isNotEmpty(refIdList)) {
            processIds = QueryChain.of(FileMetadata.class).select(FILE_METADATA.ID).where(FILE_METADATA.REF_ID.in(processIds)).listAs(String.class);
            return QueryChain.of(FileMetadata.class).where(FILE_METADATA.ID.in(processIds)).list();
        }
        return List.of();
    }
}
