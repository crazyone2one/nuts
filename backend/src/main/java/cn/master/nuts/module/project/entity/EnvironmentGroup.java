package cn.master.nuts.module.project.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 环境组 实体类。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "环境组")
@Table("environment_group")
public class EnvironmentGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 环境组id
     */
    @Id
    @Schema(description = "环境组id")
    private String id;

    /**
     * 环境组名
     */
    @Schema(description = "环境组名")
    private String name;

    /**
     * 所属项目id
     */
    @Schema(description = "所属项目id")
    private String projectId;

    /**
     * 环境组描述
     */
    @Schema(description = "环境组描述")
    private String description;

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

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Long pos;

}
