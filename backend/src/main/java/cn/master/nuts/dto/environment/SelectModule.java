package cn.master.nuts.dto.environment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Data
public class SelectModule {
    @Schema(description = "模块ID")
    private String moduleId;
    @Schema(description = "是否包含新增子模块")
    private Boolean containChildModule = false;
}
