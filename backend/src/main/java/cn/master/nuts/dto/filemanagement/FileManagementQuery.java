package cn.master.nuts.dto.filemanagement;

import cn.master.nuts.constants.StorageType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
@Data
@NoArgsConstructor
public class FileManagementQuery {
    public String projectId;
    public String keyword;
    public List<String> moduleIds;
    public String fileType;
    public String operator;
    public String storage = StorageType.MINIO.name();
    public List<String> hiddenIds = new ArrayList<>();

    public FileManagementQuery(FileBatchProcessRequest batchProcessDTO) {
        this.projectId = batchProcessDTO.getProjectId();
        this.keyword = batchProcessDTO.getCondition().getKeyword();
        this.moduleIds = batchProcessDTO.getModuleIds();
        this.fileType = batchProcessDTO.getFileType();
    }

    public FileManagementQuery(FileMetadataTableRequest batchProcessDTO) {
        this.projectId = batchProcessDTO.getProjectId();
        this.keyword = batchProcessDTO.getKeyword();
        this.moduleIds = batchProcessDTO.getModuleIds();
        this.fileType = batchProcessDTO.getFileType();
    }
}
