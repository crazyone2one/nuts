package cn.master.nuts.module.project.service.impl;

import cn.master.nuts.constants.DefaultRepositoryDir;
import cn.master.nuts.constants.ModuleConstants;
import cn.master.nuts.constants.StorageType;
import cn.master.nuts.dto.filemanagement.FileInformationResponse;
import cn.master.nuts.dto.filemanagement.FileMetadataTableRequest;
import cn.master.nuts.dto.filemanagement.FileRequest;
import cn.master.nuts.dto.filemanagement.FileUploadRequest;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.module.log.service.FileMetadataLogService;
import cn.master.nuts.module.project.entity.FileMetadata;
import cn.master.nuts.module.project.entity.FileModule;
import cn.master.nuts.module.project.mapper.FileMetadataMapper;
import cn.master.nuts.module.project.service.FileManagementService;
import cn.master.nuts.module.project.service.FileMetadataService;
import cn.master.nuts.module.system.service.FileService;
import cn.master.nuts.util.FileMetadataUtils;
import cn.master.nuts.util.TempFileUtils;
import cn.master.nuts.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.nuts.module.project.entity.table.FileMetadataTableDef.FILE_METADATA;

/**
 * 文件基础信息 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-22
 */
@Service
@RequiredArgsConstructor
public class FileMetadataServiceImpl extends ServiceImpl<FileMetadataMapper, FileMetadata> implements FileMetadataService {
    private final FileManagementService fileManagementService;
    private final FileMetadataLogService fileMetadataLogService;
    private final FileService fileService;
    private static final String JAR_FILE_PREFIX = "jar";

    @Override
    public String upload(FileUploadRequest request, String operator, MultipartFile uploadFile) throws Exception {
        // 检查模块的合法性
        fileManagementService.checkModule(request.getModuleId(), ModuleConstants.NODE_TYPE_DEFAULT);
        String fileName = StringUtils.trim(uploadFile.getOriginalFilename());

        FileMetadata fileMetadata = genFileMetadata(null, fileName, StorageType.MINIO.name(), uploadFile.getSize(), request.isEnable(), request.getProjectId(), request.getModuleId(), operator);
        mapper.insertSelective(fileMetadata);
        // 上传文件
        String filePath = uploadFile(fileMetadata, uploadFile);

        fileMetadata.setPath(filePath);
        fileMetadata.setFileVersion(fileMetadata.getId());
        mapper.update(fileMetadata);
        fileMetadataLogService.saveUploadLog(fileMetadata, operator);
        // updateChain().set(FILE_METADATA.REF_ID, fileMetadata.getId()).where(FILE_METADATA.ID.eq(fileMetadata.getId())).update();
        return fileMetadata.getId();
    }

    @Override
    public List<String> getFileType(String projectId, String storage) {
        List<String> fileTypes = queryChain().select(QueryMethods.distinct(FILE_METADATA.TYPE))
                .where(FILE_METADATA.PROJECT_ID.eq(projectId)).and(FILE_METADATA.STORAGE.eq(storage))
                .listAs(String.class);
        FileMetadataUtils.transformEmptyFileType(fileTypes);
        return fileTypes;
    }

    @Override
    public Page<FileInformationResponse> filePage(FileMetadataTableRequest request) {
        FileMetadataUtils.transformRequestFileType(request);
        Page<FileInformationResponse> page = queryChain()
                .where(FILE_METADATA.LATEST.eq(true)
                        .and(FILE_METADATA.PROJECT_ID.eq(request.getProjectId()))
                        .and(FILE_METADATA.NAME.like(request.getKeyword()))
                        .and(FILE_METADATA.STORAGE.eq(request.getStorage()))
                        .and(FILE_METADATA.MODULE_ID.in(request.getModuleIds()))
                        .and(FILE_METADATA.TYPE.eq(request.getFileType()))
                        .and(FILE_METADATA.REF_ID.notIn(request.getHiddenIds())))
                .orderBy(FILE_METADATA.UPDATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), FileInformationResponse.class);
        initModuleName(page.getRecords());
        return page;
    }

    @Override
    public ResponseEntity<byte[]> downloadById(String id) {
        FileMetadata fileMetadata = mapper.selectOneById(id);
        byte[] bytes = getFileByte(fileMetadata);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + FileMetadataUtils.getFileName(fileMetadata) + "\"")
                .body(bytes);
    }

    @Override
    public byte[] getFileByte(FileMetadata fileMetadata) {
        String filePath = null;
        try {
            filePath = TempFileUtils.createFile(TempFileUtils.getTmpFilePath(fileMetadata.getId()), fileManagementService.getFile(fileMetadata));
        } catch (Exception ignore) {
        }
        return TempFileUtils.getFile(filePath);
    }

    private void initModuleName(List<FileInformationResponse> records) {
        List<String> moduleIds = records.stream().map(FileInformationResponse::getModuleId).distinct().toList();
        Map<String, String> moduleNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(moduleIds)) {
            List<FileModule> moduleList = QueryChain.of(FileModule.class).where(FileModule::getId).in(moduleIds).list();
            moduleNameMap = moduleList.stream().collect(Collectors.toMap(FileModule::getId, FileModule::getName));
        }
        for (FileInformationResponse record : records) {
            if (Strings.CI.equals(record.getModuleId(), ModuleConstants.DEFAULT_NODE_ID)) {
                record.setModuleName(Translator.get("file.module.default.name"));
            } else {
                record.setModuleName(moduleNameMap.get(record.getModuleId()));
            }
        }

    }

    private String uploadFile(FileMetadata fileMetadata, MultipartFile file) throws Exception {
        String filePath;
        FileRequest uploadFileRequest = new FileRequest();
        try (InputStream inputStream = file.getInputStream()) {
            uploadFileRequest.setFileName(fileMetadata.getId());
            uploadFileRequest.setFolder(generateMinIOFilePath(fileMetadata.getProjectId()));
            uploadFileRequest.setStorage(StorageType.MINIO.name());
            filePath = fileService.upload(inputStream, uploadFileRequest);
        }
        if (TempFileUtils.isImage(fileMetadata.getType())) {
            try (InputStream inputStream = file.getInputStream()) {
                // 图片文件自动生成预览图
                byte[] previewImg = TempFileUtils.compressPic(inputStream);
                if (previewImg.length > 0) {
                    uploadFileRequest.setFolder(DefaultRepositoryDir.getFileManagementPreviewDir(fileMetadata.getProjectId()));
                    fileService.upload(previewImg, uploadFileRequest);
                }
            }
        }
        return filePath;
    }

    private String generateMinIOFilePath(String projectId) {
        return DefaultRepositoryDir.getFileManagementDir(projectId);
    }

    private FileMetadata genFileMetadata(String fileSpecifyName, String filePath, String storage, long size, boolean enable, String projectId, String moduleId, String operator) {
        FileMetadata fileMetadata = new FileMetadata();
        parseAndSetFileNameType(filePath, fileMetadata);
        // 如果开启了开关，检查是否是jar文件
        if (enable) {
            checkEnableFile(fileMetadata.getType());
        }
        // 指定了文件名称，则替换原文件名
        if (StringUtils.isNotBlank(fileSpecifyName)) {
            fileMetadata.setName(fileSpecifyName);
        }
        // 检查处理后的用户名合法性
        if (Strings.CS.equals(storage, StorageType.MINIO.name())) {
            checkMinIOFileName(null, fileMetadata.getName(), fileMetadata.getType(), projectId);
        }
        fileMetadata.setStorage(storage);
        fileMetadata.setProjectId(projectId);
        fileMetadata.setModuleId(moduleId);
        fileMetadata.setCreateUser(operator);
        fileMetadata.setUpdateUser(operator);
        fileMetadata.setSize(size);
        fileMetadata.setPath(filePath);
        fileMetadata.setLatest(true);
        fileMetadata.setRefId(fileMetadata.getId());
        fileMetadata.setEnable(enable);
        return fileMetadata;
    }

    private void checkMinIOFileName(String id, String fileName, String type, String projectId) {
        if (StringUtils.isBlank(fileName)) {
            throw new NSException(Translator.get("file.name.cannot.be.empty"));
        }
        QueryChain<FileMetadata> queryChain = queryChain().where(FILE_METADATA.NAME.eq(fileName).and(FILE_METADATA.TYPE.eq(type))
                .and(FILE_METADATA.LATEST.eq(true)).and(FILE_METADATA.PROJECT_ID.eq(projectId))
                .and(FILE_METADATA.STORAGE.eq(StorageType.MINIO.name())));
        if (!StringUtils.isBlank(id)) {
            queryChain.and(FILE_METADATA.ID.ne(id));
        }
        if (mapper.selectCountByQuery(queryChain) > 0) {
            throw new NSException(Translator.get("file.name.exist") + ":" + fileName);
        }
    }

    private void checkEnableFile(String fileType) {
        if (!Strings.CI.equals(fileType, JAR_FILE_PREFIX)) {
            throw new NSException(Translator.get("file.not.jar"));
        }
    }

    private void parseAndSetFileNameType(String filePath, @NotNull FileMetadata fileMetadata) {
        String fileName = TempFileUtils.getFileNameByPath(filePath);
        if (FileMetadataUtils.isUnknownFile(fileName)) {
            fileMetadata.setOriginalName(fileName);
            fileMetadata.setName(fileName);
            fileMetadata.setType(StringUtils.EMPTY);
        } else {
            // 采用这种判断方式，可以避免将隐藏文件的后缀名作为文件类型
            fileMetadata.setOriginalName(fileName);
            fileMetadata.setName(StringUtils.substring(fileName, 0, fileName.lastIndexOf(".")));
            fileMetadata.setType(StringUtils.substring(fileName, fileName.lastIndexOf(".") + 1));
        }
    }

}
