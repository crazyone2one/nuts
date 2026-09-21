package cn.master.nuts.dto.system;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TaskParameterRequest {
    @NotBlank
    private String id;

    @Valid
    private Map<String, TaskParameterItem> parameters = new LinkedHashMap<>();
}
