package cn.master.nuts.dto.environment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/9/18, 星期五
 **/
@Data
public class FtpConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "id")
    private String id;
    private String ip;
    private String port;
    private String localPath;
    private String remotePath;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    private String description;
}