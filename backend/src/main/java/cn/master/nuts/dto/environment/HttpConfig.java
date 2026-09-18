package cn.master.nuts.dto.environment;

import cn.master.nuts.constants.ValueEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;
import org.apache.commons.lang3.Strings;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Data
public class HttpConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "id")
    private String id;

    @Schema(description = "http协议类型(http/https)")
    private String protocol = HttpProtocolType.HTTP.name();

    @Schema(description = "环境域名")
    private String hostname;

    @Schema(description = "完整url")
    private String url;
    /**
     * 启用条件
     * {@link HttpConfigMatchType}
     */
    @Schema(description = "启用条件  NONE/MODULE/PATH")
    private String type = HttpConfigMatchType.NONE.name();
    @Valid
    @Schema(description = "路径匹配规则")
    private HttpConfigPathMatchRule pathMatchRule = new HttpConfigPathMatchRule();
    @Valid
    @Schema(description = "模块匹配规则")
    private HttpConfigModuleMatchRule moduleMatchRule = new HttpConfigModuleMatchRule();

    @Schema(description = "请求头")
    private List<@Valid KeyValueEnableParam> headers = new ArrayList<>(0);

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private int order;

    @Schema(description = "认证配置")
    // private HTTPAuthConfig authConfig = new HTTPAuthConfig();


    public boolean isModuleMatchRule() {
        return Strings.CS.equals(HttpConfigMatchType.MODULE.name(), type);
    }

    public boolean isPathMatchRule() {
        return Strings.CS.equals(HttpConfigMatchType.PATH.name(), type);
    }

    public int getModuleMatchRuleOrder() {
        if (isPathMatchRule()) {
            return 0;
        } else if (isModuleMatchRule()) {
            return 1;
        }
        return 2;
    }

    /**
     * 启用条件匹配类型
     */
    public enum HttpConfigMatchType {
        /**
         * 路径匹配
         */
        PATH,
        /**
         * 模块匹配
         */
        MODULE,
        /**
         * 无条件
         */
        NONE
    }

    /**
     * 启用条件匹配类型
     */
    public enum HttpProtocolType implements ValueEnum {
        HTTP("http"),
        HTTPS("https");

        private final String value;

        HttpProtocolType(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }
}
