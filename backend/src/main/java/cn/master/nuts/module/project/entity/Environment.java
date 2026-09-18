package cn.master.nuts.module.project.entity;

import cn.master.nuts.handler.validation.Created;
import cn.master.nuts.handler.validation.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 环境 实体类。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "环境")
@Table("environment")
public class Environment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 环境ID
     */
    @Id
    @Schema(description = "环境ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{environment.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{environment.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "环境名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{environment.name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{environment.name.length_range}", groups = {Created.class, Updated.class})
    private String name;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{environment.project_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{environment.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否是mock环境", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{environment.mock.not_blank}", groups = {Created.class})
    private Boolean mock;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{environment.pos.not_blank}", groups = {Created.class})
    private Long pos;

    @Schema(description = "Config Data (JSON format)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{environment_blob.config.not_blank}", groups = {Created.class})
    private byte[] config;

}
