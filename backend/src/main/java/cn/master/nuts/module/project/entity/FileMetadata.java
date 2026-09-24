package cn.master.nuts.module.project.entity;

import cn.master.nuts.handler.listener.RefIdInsertListener;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件基础信息 实体类。
 *
 * @author 11's papa
 * @since 2026-09-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件基础信息")
@Table(value = "file_metadata", onInsert = RefIdInsertListener.class)
public class FileMetadata implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @Id
    @Schema(description = "文件ID")
    private String id;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String name;

    /**
     * 原始名（含后缀）
     */
    @Schema(description = "原始名（含后缀）")
    private String originalName;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String type;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long size;

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
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 文件存储方式
     */
    @Schema(description = "文件存储方式")
    private String storage;

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
     * 标签
     */
    @Schema(description = "标签")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 文件所属模块
     */
    @Schema(description = "文件所属模块")
    private String moduleId;

    /**
     * 文件存储路径
     */
    @Schema(description = "文件存储路径")
    private String path;

    /**
     * 是否是最新版
     */
    @Schema(description = "是否是最新版")
    private Boolean latest;

    /**
     * 启用/禁用;启用禁用（一般常用于jar文件）
     */
    @Schema(description = "启用/禁用;启用禁用（一般常用于jar文件）")
    private Boolean enable;

    /**
     * 同版本数据关联的ID
     */
    @Schema(description = "同版本数据关联的ID")
    private String refId;

    /**
     * 文件版本号
     */
    @Schema(description = "文件版本号")
    private String fileVersion;

}
