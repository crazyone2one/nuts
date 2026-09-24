package cn.master.nuts.dto.filemanagement;

import cn.master.nuts.constants.StorageType;
import cn.master.nuts.dto.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class FileMetadataTableRequest extends BasePageRequest {
    @Schema(description = "模块ID(根据模块树查询时要把当前节点以及子节点都放在这里。)")
    private List<String> moduleIds;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "项目ID")
    @NotBlank(message = "{id must not be blank}")
    private String projectId;
    private String storage = StorageType.MINIO.name();
    private List<String> hiddenIds = new ArrayList<>();
}
