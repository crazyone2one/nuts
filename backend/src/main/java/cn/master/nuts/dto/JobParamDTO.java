package cn.master.nuts.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
@Data
public class JobParamDTO {
    private String sensorCode;
    private String beginTime;
    private String endTime;

    private Map<String, Object> extra = new LinkedHashMap<>();
    @JsonAnySetter
    public void setExtra(String name, Object value) {
        extra.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return extra;
    }
}
