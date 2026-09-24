package cn.master.nuts.module.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统参数 实体类。
 *
 * @author 11's papa
 * @since 2026-09-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统参数")
@Table("system_parameter")
public class SystemParameter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源池ID
     */
    @Id
    @Schema(description = "资源池ID")
    private String id;

    /**
     * 参数名称
     */
    @Id
    @Schema(description = "参数名称")
    private String paramKey;

    /**
     * 参数值
     */
    @Schema(description = "参数值")
    private String paramValue;

    /**
     * 类型
     */
    @Schema(description = "类型")
    private String type;

}
