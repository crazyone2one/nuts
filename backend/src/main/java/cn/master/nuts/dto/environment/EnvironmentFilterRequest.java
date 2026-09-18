package cn.master.nuts.dto.environment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
public record EnvironmentFilterRequest(
        @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{project_application.project_id.not_blank}")
        @Size(min = 1, max = 50, message = "{project_parameters.project_id.length_range}")
        String projectId,
        @Schema(description = "关键字")
        String keyword
) {
}
