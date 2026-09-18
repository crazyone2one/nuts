package cn.master.nuts.module.project.entity;

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
 * 环境组关联关系 实体类。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "环境组关联关系")
@Table("environment_group_relation")
public class EnvironmentGroupRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "")
    private String id;

    /**
     * 环境组id
     */
    @Schema(description = "环境组id")
    private String environmentGroupId;

    /**
     * 环境ID
     */
    @Schema(description = "环境ID")
    private String environmentId;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private String projectId;

}
