package cn.master.nuts.module.system.entity;

import cn.master.nuts.handler.result.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户 实体类。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户")
@Table("user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Id
    @Schema(description = "用户ID")
    @JsonView(Views.Base.class)
    private String id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @JsonView(Views.Base.class)
    private String name;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱")
    @JsonView(Views.Base.class)
    private String email;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码")
    private String password;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    @JsonView(Views.Public.class)
    private Boolean enable;

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
     * 语言
     */
    @Schema(description = "语言")
    private String language;

    /**
     * 当前组织ID
     */
    @Schema(description = "当前组织ID")
    @JsonView(Views.Internal.class)
    private String lastOrganizationId;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @JsonView(Views.Public.class)
    private String phone;

    /**
     * 来源：LOCAL OIDC CAS OAUTH2
     */
    @Schema(description = "来源：LOCAL OIDC CAS OAUTH2")
    private String source;

    /**
     * 当前项目ID
     */
    @Schema(description = "当前项目ID")
    @JsonView(Views.Internal.class)
    private String lastProjectId;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @JsonView(Views.Public.class)
    private String createUser;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    @JsonView(Views.Public.class)
    private String updateUser;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

    /**
     * 其他平台对接信息
     */
    @Schema(description = "其他平台对接信息")
    private byte[] platformInfo;

    /**
     * 头像
     */
    @Schema(description = "头像")
    @JsonView(Views.Public.class)
    private String avatar;

}
