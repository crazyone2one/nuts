package cn.master.nuts.dto.system;

import cn.master.nuts.dto.JobParamDTO;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/9/21, 星期一
 **/
@Data
public class ScheduleParamDTO {
    private String id;
    private JobParamDTO config;
}
