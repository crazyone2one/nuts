package cn.master.nuts.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "任务参数")
public class TaskParameterItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String label;

    @NotBlank
    private String key;

    @NotBlank
    private String type;

    @NotNull
    private Object value;

    @NotNull
    private Boolean enabled;
}
