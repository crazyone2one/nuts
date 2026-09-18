package cn.master.nuts.dto.environment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Data
public class KeyValueParam {
    /**
     * 键
     */
    private String key;
    /**
     * 值
     */
    private String value;

    @JsonIgnore
    public boolean isValid() {
        return StringUtils.isNotBlank(key);
    }

    @JsonIgnore
    public boolean isNotBlankValue() {
        return StringUtils.isNotBlank(value);
    }
}
