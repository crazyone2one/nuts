package cn.master.nuts.module.project.controller;

import cn.master.nuts.constants.StorageType;
import cn.master.nuts.dto.filemanagement.FileBatchProcessRequest;
import cn.master.nuts.dto.filemanagement.FileInformationResponse;
import cn.master.nuts.dto.filemanagement.FileMetadataTableRequest;
import cn.master.nuts.dto.filemanagement.FileUploadRequest;
import cn.master.nuts.module.project.service.FileManagementService;
import cn.master.nuts.module.project.service.FileMetadataService;
import cn.master.nuts.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/23, 星期三
 **/
@Tag(name = "项目管理-文件管理-文件")
@RestController
@RequiredArgsConstructor
@RequestMapping("/project/file")
public class FileManagementController {
    private final FileMetadataService fileMetadataService;
    private final FileManagementService fileManagementService;

    @GetMapping(value = "/type/{projectId}")
    @Operation(summary = "项目管理-文件管理-获取已存在的文件类型")
    public List<String> getFileType(@PathVariable String projectId) {
        return fileMetadataService.getFileType(projectId, StorageType.MINIO.name());
    }

    @PostMapping("/page")
    @Operation(summary = "项目管理-文件管理-表格分页查询文件")
    public Page<FileInformationResponse> page(@Validated @RequestBody FileMetadataTableRequest request) {
        return fileMetadataService.filePage(request);
    }

    @PostMapping("/upload")
    @Operation(summary = "项目管理-文件管理-上传文件")
    public String upload(@Validated @RequestPart("request") FileUploadRequest request, @RequestPart(value = "file", required = false) MultipartFile uploadFile) throws Exception {
        return fileMetadataService.upload(request, SessionUtils.getUserId(), uploadFile);
    }

    @GetMapping(value = "/download/{id}")
    @Operation(summary = "项目管理-文件管理-下载文件")
    // @RequiresPermissions(PermissionConstants.PROJECT_FILE_MANAGEMENT_READ_DOWNLOAD)
    // @CheckOwner(resourceId = "#id", resourceType = "file_metadata")
    public ResponseEntity<byte[]> download(@PathVariable String id) throws Exception {
        return fileMetadataService.downloadById(id);
    }
    @PostMapping(value = "/delete")
    @Operation(summary = "项目管理-文件管理-删除文件")
    // @RequiresPermissions(PermissionConstants.PROJECT_FILE_MANAGEMENT_READ_DELETE)
    // @CheckOwner(resourceId = "#request.getSelectIds()", resourceType = "file_metadata")
    public void delete(@Validated @RequestBody FileBatchProcessRequest request) throws Exception {
        fileManagementService.delete(request, SessionUtils.getUserId());
    }
}
