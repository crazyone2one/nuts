package cn.master.nuts.dto.filemanagement;

import cn.master.nuts.dto.TableBatchProcessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class FileBatchProcessRequest extends TableBatchProcessDTO {

    @Schema(description = "项目ID")
    @NotBlank(message = "{id must not be blank}")
    private String projectId;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "模块ID")
    private List<String> moduleIds;

}
