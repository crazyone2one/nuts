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
 * 自定义字段选项 实体类。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "自定义字段选项")
@Table("custom_field_option")
public class CustomFieldOption implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自定义字段ID
     */
    @Id
    @Schema(description = "自定义字段ID")
    private String id;

    /**
     * 自定义字段ID
     */
    @Id
    @Schema(description = "自定义字段ID")
    private String fieldId;

    /**
     * 选项值
     */
    @Id
    @Schema(description = "选项值")
    private String value;

    /**
     * 选项值名称
     */
    @Schema(description = "选项值名称")
    private String text;

    /**
     * 是否内置
     */
    @Schema(description = "是否内置")
    private Boolean internal;

    /**
     * 自定义排序，间隔1
     */
    @Schema(description = "自定义排序，间隔1")
    private Integer pos;

}
