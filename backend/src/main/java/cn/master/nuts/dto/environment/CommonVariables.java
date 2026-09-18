package cn.master.nuts.dto.environment;

import cn.master.nuts.constants.VariableTypeConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonVariables extends KeyValueParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "id")
    private String id;
    @Schema(description = "变量类型 CONSTANT LIST JSON")
    private String paramType = VariableTypeConstants.CONSTANT.name();
    @Schema(description = "状态")
    private Boolean enable = true;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "标签")
    private List<String> tags;


    @JsonIgnore
    public boolean isConstantValid() {
        return (Strings.CS.equals(this.paramType, VariableTypeConstants.CONSTANT.name()) || StringUtils.isBlank(paramType))
                && isValid();
    }

    @JsonIgnore
    public boolean isListValid() {
        return Strings.CS.equals(this.paramType, VariableTypeConstants.LIST.name()) && isValid() && isNotBlankValue() && getValue().contains(",");
    }

    @JsonIgnore
    public boolean isJsonValid() {
        return Strings.CS.equals(this.paramType, VariableTypeConstants.JSON.name()) && isValid();
    }

}
