package cn.master.nuts.module.project.service;

import cn.master.nuts.dto.filemanagement.FileInformationResponse;
import cn.master.nuts.dto.filemanagement.FileMetadataTableRequest;
import cn.master.nuts.dto.filemanagement.FileUploadRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.nuts.module.project.entity.FileMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件基础信息 服务层。
 *
 * @author 11's papa
 * @since 2026-09-22
 */
public interface FileMetadataService extends IService<FileMetadata> {
    String upload(FileUploadRequest request, String operator, MultipartFile uploadFile) throws Exception;

    List<String> getFileType(String projectId, String name);

    Page<FileInformationResponse> filePage(FileMetadataTableRequest request);

    ResponseEntity<byte[]> downloadById(String id);

    byte[] getFileByte(FileMetadata fileMetadata);
}
