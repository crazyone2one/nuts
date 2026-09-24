package cn.master.nuts.dto.filemanagement;

import cn.master.nuts.constants.ModuleConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
@Data
public class FileUploadRequest {
    @Schema(description = "项目Id")
    @NotBlank(message = "{project.id.not_blank}")
    private String projectId;

    @Schema(description = "模块Id")
    @NotBlank(message = "{file_module.id.not_blank}")
    private String moduleId = ModuleConstants.DEFAULT_NODE_ID;

    @Schema(description = "是否启用")
    private boolean enable;
}
