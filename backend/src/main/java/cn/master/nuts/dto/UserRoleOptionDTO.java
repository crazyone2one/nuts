package cn.master.nuts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/9/11, 星期五
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class UserRoleOptionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private String id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "用户id")
    private String userId;
}
