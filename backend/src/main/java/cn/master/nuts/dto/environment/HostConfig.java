package cn.master.nuts.dto.environment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Data
public class HostConfig implements Serializable {
    @Schema(description = "启用Host")
    private Boolean enable;
    @Schema(description = "Host列表")
    private List<Host> hosts = new ArrayList<>(0);
    @Serial
    private static final long serialVersionUID = 1L;

}
