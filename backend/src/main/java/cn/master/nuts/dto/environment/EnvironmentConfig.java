package cn.master.nuts.dto.environment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Data
public class EnvironmentConfig {
    @Schema(description = "环境变量")
    private List<CommonVariables> commonVariables = new ArrayList<>(0);
    @Schema(description = "HTTP配置")
    private List<HttpConfig> httpConfig = new ArrayList<>(0);
    @Schema(description = "数据库配置")
    private List<DataSource> dataSources = new ArrayList<>(0);

    @Schema(description = "Host配置")
    private HostConfig hostConfig = new HostConfig();
    @Schema(description = "FTP配置")
    private FtpConfig ftpConfig = new FtpConfig();
}
