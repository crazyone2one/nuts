package cn.master.nuts.dto.environment;

import cn.master.nuts.handler.validation.Created;
import cn.master.nuts.handler.validation.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Data
public class EnvironmentRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "ID")
    @NotBlank(message = "{project_parameters.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{project_parameters.id.length_range}", groups = {Updated.class})
    private String id;
    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{project_application.project_id.not_blank}", groups = {Created.class, Updated.class})
    @Size(min = 1, max = 50, message = "{project_parameters.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;
    @Schema(description = "环境名称")
    @NotBlank(message = "{environment_name_is_null}", groups = {Created.class, Updated.class})
    private String name;
    @Schema(description = "环境配置")
    private EnvironmentConfig config;
    @Schema(description = "描述")
    private String description;

}
