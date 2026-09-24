package cn.master.nuts.module.project.controller;

import cn.master.nuts.dto.BaseTreeNode;
import cn.master.nuts.dto.filemanagement.FileModuleCreateRequest;
import cn.master.nuts.dto.filemanagement.FileModuleUpdateRequest;
import cn.master.nuts.module.project.service.FileModuleService;
import cn.master.nuts.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件管理模块 控制层。
 *
 * @author 11's papa
 * @since 2026-09-22
 */
@RestController
@Tag(name = "文件管理模块接口")
@RequiredArgsConstructor
@RequestMapping("/fileModule")
public class FileModuleController {

    private final FileModuleService fileModuleService;

    @PostMapping("save")
    @Operation(summary = "项目管理-文件管理-模块-添加模块")
    public String save(@RequestBody @Parameter(description = "文件管理模块") @Validated FileModuleCreateRequest request) {
        return fileModuleService.add(request, SessionUtils.getUserId());
    }

    @GetMapping("remove/{id}")
    @Operation(summary = "项目管理-文件管理-模块-删除模块")
    public void remove(@PathVariable @Parameter(description = "文件管理模块主键") String id) {
        fileModuleService.deleteModule(id, SessionUtils.getUserId());
    }


    @PostMapping("update")
    @Operation(summary = "项目管理-文件管理-模块-修改模块")
    public void update(@RequestBody @Validated FileModuleUpdateRequest request) {
        fileModuleService.update(request, SessionUtils.getUserId());
    }

    /**
     * 查询所有文件管理模块。
     *
     * @return 所有数据
     */
    @GetMapping("/tree/{projectId}")
    @Operation(summary = "项目管理-文件管理-模块-查找模块")
    public List<BaseTreeNode> list(@PathVariable String projectId) {
        return fileModuleService.getTree(projectId);
    }
}
