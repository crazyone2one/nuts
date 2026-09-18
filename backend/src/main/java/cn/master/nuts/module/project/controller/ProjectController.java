package cn.master.nuts.module.project.controller;

import cn.master.nuts.dto.UserExtendDTO;
import cn.master.nuts.dto.system.ProjectSwitchRequest;
import cn.master.nuts.dto.system.UserDTO;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.service.ProjectService;
import cn.master.nuts.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/14, 星期一
 **/
@RestController
@Tag(name = "项目管理")
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/list/options/{organizationId}")
    @Operation(summary = "根据组织ID获取所有有权限的项目")
    public List<Project> getUserProject(@PathVariable String organizationId) {
        return projectService.getUserProject(organizationId, SessionUtils.getUserId());
    }

    @PostMapping("/switch")
    @Operation(summary = "切换项目")
    public UserDTO switchProject(@RequestBody ProjectSwitchRequest request) {
        return projectService.switchProject(request, SessionUtils.getUserId());
    }

    @GetMapping("/list/options/{organizationId}/{module}")
    @Operation(summary = "根据组织ID获取所有开启某个模块的所有有权限的项目")
    public List<Project> getUserProjectWidthModule(@PathVariable String organizationId, @PathVariable String module) {
        return projectService.getUserProjectWidthModule(organizationId, module, SessionUtils.getUserId());
    }

    @GetMapping("/get-member/option/{projectId}")
    @Operation(summary = "项目管理-获取成员下拉选项")
    public List<UserExtendDTO> getMemberOption(@PathVariable String projectId,
                                               @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                               @RequestParam(value = "keyword", required = false) String keyword) {
        return projectService.getMemberOption(projectId, keyword);
    }
}
