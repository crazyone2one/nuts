package cn.master.nuts.module.project.controller;

import cn.master.nuts.dto.BasePageRequest;
import cn.master.nuts.dto.system.ScheduleParamDTO;
import cn.master.nuts.dto.system.TaskHubScheduleDTO;
import cn.master.nuts.module.log.constants.OperationLogModule;
import cn.master.nuts.module.system.service.BaseTaskHubService;
import cn.master.nuts.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
@Tag(name = "项目任务中心")
@RestController
@RequiredArgsConstructor
@RequestMapping("/project/task-center")
public class ProjectTaskHubController {
    private final BaseTaskHubService baseTaskHubService;

    @PostMapping("/schedule/page")
    @Operation(summary = "项目-任务中心-后台执行任务列表")
    public Page<TaskHubScheduleDTO> scheduleList(@Validated @RequestBody BasePageRequest request) {
        return baseTaskHubService.getScheduleTaskList(request, List.of(SessionUtils.getCurrentProjectId()));
    }

    @GetMapping("/schedule/switch/{id}")
    @Operation(summary = "项目-任务中心-后台任务开启关闭")
    // @RequiresPermissions(PermissionConstants.PROJECT_SCHEDULE_TASK_CENTER_READ_UPDATE)
    public void enable(@PathVariable String id) {
        baseTaskHubService.enable(id, SessionUtils.getUserId(), "/project/task-center/schedule/switch/", OperationLogModule.PROJECT_MANAGEMENT_TASK_CENTER);
    }

    @GetMapping("/schedule/delete/{id}")
    @Operation(summary = "项目-任务中心-系统后台任务-删除")
    // @RequiresPermissions(PermissionConstants.PROJECT_SCHEDULE_TASK_CENTER_READ_DELETE)
    public void deleteScheduleTask(@PathVariable String id) {
        baseTaskHubService.deleteScheduleTask(id, SessionUtils.getUserId(), "/project/task-center/schedule/delete/", OperationLogModule.PROJECT_MANAGEMENT_TASK_CENTER);
    }

    @GetMapping("/schedule/getParam/{id}")
    @Operation(summary = "项目-任务中心-后台任务开启关闭")
    // @RequiresPermissions(PermissionConstants.PROJECT_SCHEDULE_TASK_CENTER_READ_UPDATE)
    public ScheduleParamDTO getParam(@PathVariable String id) {
        return baseTaskHubService.getScheduleParamDTO(id);
    }
}
