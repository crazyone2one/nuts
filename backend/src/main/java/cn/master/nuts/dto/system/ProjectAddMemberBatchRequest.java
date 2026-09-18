package cn.master.nuts.dto.system;

import cn.master.nuts.handler.validation.Created;
import cn.master.nuts.handler.validation.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/11, 星期五
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectAddMemberBatchRequest extends ProjectAddMemberRequest {
    @Schema(description = "项目ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<
            @NotBlank(message = "{project.id.not_blank}", groups = {Created.class, Updated.class})
                    String> projectIds;
}
