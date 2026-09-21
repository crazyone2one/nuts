package cn.master.nuts.dto.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TaskParameterConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 使用参数 key 作为 Map key，保证数据库中的任务参数天然按 key 唯一存储。
     */
    private Map<String, TaskParameterItem> parameters = new LinkedHashMap<>();
}
