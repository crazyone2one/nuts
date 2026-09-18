package cn.master.nuts.module.system.controller;

import cn.master.nuts.dto.ProjectDTO;
import cn.master.nuts.dto.system.AddProjectRequest;
import cn.master.nuts.dto.system.ProjectRequest;
import cn.master.nuts.dto.system.UpdateProjectNameRequest;
import cn.master.nuts.dto.system.UpdateProjectRequest;
import cn.master.nuts.handler.validation.Created;
import cn.master.nuts.handler.validation.Updated;
import cn.master.nuts.module.log.annotation.Log;
import cn.master.nuts.module.log.constants.OperationLogType;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.entity.User;
import cn.master.nuts.module.system.service.ProjectService;
import cn.master.nuts.module.system.service.SystemProjectLogService;
import cn.master.nuts.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.master.nuts.module.system.entity.table.ProjectTableDef.PROJECT;
import static cn.master.nuts.module.system.entity.table.UserTableDef.USER;

/**
 * 项目 控制层。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@RestController
@Tag(name = "项目接口")
@RequiredArgsConstructor
@RequestMapping("/system/project")
public class SystemProjectController {

    private final ProjectService projectService;

    @PostMapping("save")
    @Operation(description = "保存项目")
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = SystemProjectLogService.class)
    public void save(@RequestBody @Validated({Created.class}) AddProjectRequest request) {
        projectService.add(request, SessionUtils.getUserId());
    }

    /**
     * 根据主键删除项目。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @GetMapping("remove/{id}")
    @Operation(description = "根据主键删除项目")
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#id)", msClass = SystemProjectLogService.class)
    public boolean remove(@PathVariable @Parameter(description = "项目主键") String id) {
        return projectService.delete(id, SessionUtils.getUserId());
    }

    /**
     * 根据主键更新项目。
     *
     * @param request 项目
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    @Operation(description = "根据主键更新项目")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#request)", msClass = SystemProjectLogService.class)
    public int update(@RequestBody @Validated({Updated.class}) UpdateProjectRequest request) {
        return projectService.updateProject(request, SessionUtils.getUserId());
    }

    /**
     * 查询所有项目。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有项目")
    public List<Project> list(@Schema(description = "查询关键字，根据项目名查询", requiredMode = Schema.RequiredMode.REQUIRED) @RequestParam(value = "keyword", required = false) String keyword) {
        return QueryChain.of(Project.class)
                .select(PROJECT.ID, PROJECT.NAME)
                .where(PROJECT.NAME.like(keyword)).and(PROJECT.ENABLE.eq(true))
                .orderBy(Project::getUpdateTime).desc().limit(1000)
                .list();
    }

    /**
     * 根据主键获取项目。
     *
     * @param id 项目主键
     * @return 项目详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取项目")
    public Project getInfo(@PathVariable @Parameter(description = "项目主键") String id) {
        return projectService.getById(id);
    }

    /**
     * 分页查询项目。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    @Operation(description = "分页查询项目")
    public Page<ProjectDTO> page(@Validated @RequestBody ProjectRequest request) {
        return projectService.page(request);
    }

    @GetMapping("/enable/{id}")
    @Operation(summary = "系统设置-系统-组织与项目-项目-启用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#id)", msClass = SystemProjectLogService.class)
    public void enable(@PathVariable String id) {
        projectService.enable(id, SessionUtils.getUserId());
    }

    @GetMapping("/disable/{id}")
    @Operation(summary = "系统设置-系统-组织与项目-项目-禁用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#id)", msClass = SystemProjectLogService.class)
    public void disable(@PathVariable String id) {
        projectService.disable(id, SessionUtils.getUserId());
    }

    @PostMapping("/rename")
    @Operation(summary = "系统设置-系统-组织与项目-项目-修改项目名称")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.renameLog(#request)", msClass = SystemProjectLogService.class)
    public void rename(@RequestBody @Validated({Updated.class}) UpdateProjectNameRequest request) {
        projectService.rename(request, SessionUtils.getUserId());
    }

    @GetMapping("/user-list")
    @Operation(summary = "系统设置-系统-组织与项目-项目-系统-组织及项目, 获取管理员下拉选项")
    public List<User> getUserList(@Schema(description = "查询关键字，根据邮箱和用户名查询")
                                  @RequestParam(value = "keyword", required = false) String keyword) {
        return QueryChain.of(User.class).select(QueryMethods.distinct(USER.ID, USER.EMAIL, USER.NAME))
                .select(USER.CREATE_TIME)
                .where(USER.NAME.like(keyword))
                .orderBy(USER.CREATE_TIME.desc())
                .limit(1000)
                .list();
    }
}
