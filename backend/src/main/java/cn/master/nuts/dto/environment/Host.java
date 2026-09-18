package cn.master.nuts.dto.environment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Data
public class Host implements Serializable {
    @Schema(description = "IP")
    private String ip;
    @Schema(description = "域名")
    private String domain;
    @Schema(description = "描述")
    private String description;

    @Serial
    private static final long serialVersionUID = 1L;
}
