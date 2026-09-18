package cn.master.nuts.dto.environment;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class KeyValueEnableParam extends KeyValueParam {
    /**
     * 是否启用
     * 默认启用
     */
    private Boolean enable = true;
    /**
     * 描述
     */
    private String description;
}
