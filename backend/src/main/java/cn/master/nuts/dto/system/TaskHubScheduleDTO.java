package cn.master.nuts.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
@Data
public class TaskHubScheduleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "所属组织")
    private String organizationName;

    @Schema(description = "所属项目")
    private String projectName;

    @Schema(description = "项目id")
    private String projectId;

    @Schema(description = "组织id")
    private String organizationId;

    @Schema(description = "任务id")
    private String id;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "资源Id")
    private String resourceId;

    @Schema(description = "资源业务id")
    private Long num;

    @Schema(description = "类型")
    private String resourceType;

    @Schema(description = "运行规则（cron表达式）")
    private String cronExpression;

    @Schema(description = "上次完成时间")
    private Long lastTime;

    @Schema(description = "下次执行时间")
    @JsonFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private Long nextTime;

    @Schema(description = "任务状态")
    private boolean enable;

    @Schema(description = "操作人")
    private String createUserId;

    @Schema(description = "操作人")
    private String createUserName;

    @Schema(description = "操作时间")
    private LocalDateTime createTime;

    @Schema(description = "资源num")
    private String resourceNum;

}
