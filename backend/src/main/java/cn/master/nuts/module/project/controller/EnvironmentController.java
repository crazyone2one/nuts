package cn.master.nuts.module.project.controller;

import cn.master.nuts.dto.OptionDTO;
import cn.master.nuts.dto.environment.DataSource;
import cn.master.nuts.dto.environment.EnvironmentFilterRequest;
import cn.master.nuts.dto.environment.EnvironmentInfoDTO;
import cn.master.nuts.dto.environment.EnvironmentRequest;
import cn.master.nuts.handler.validation.Created;
import cn.master.nuts.handler.validation.Updated;
import cn.master.nuts.module.log.annotation.Log;
import cn.master.nuts.module.log.constants.OperationLogType;
import cn.master.nuts.module.log.service.EnvironmentLogService;
import cn.master.nuts.module.project.entity.Environment;
import cn.master.nuts.module.project.service.EnvironmentService;
import cn.master.nuts.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 环境 控制层。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@RestController
@Tag(name = "环境接口")
@RequiredArgsConstructor
@RequestMapping(value = "/project/environment")
public class EnvironmentController {

    private final EnvironmentService environmentService;

    /**
     * 保存环境。
     *
     * @param request  环境请求
     * @param sslFiles SSL文件
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description = "保存环境")
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = EnvironmentLogService.class)
    public Environment add(@Validated({Created.class}) @RequestPart(value = "request") EnvironmentRequest request,
                           @RequestPart(value = "file", required = false) List<MultipartFile> sslFiles) {
        return environmentService.add(request, SessionUtils.getUserId(), sslFiles);
    }

    /**
     * 根据主键删除环境。
     *
     * @param id 主键
     */
    @GetMapping("remove/{id}")
    @Operation(description = "根据主键删除环境")
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#id)", msClass = EnvironmentLogService.class)
    public void remove(@PathVariable @Parameter(description = "环境主键") String id) {
        environmentService.delete(id);
    }

    /**
     * 根据主键更新环境。
     *
     * @param request 环境
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    @Operation(description = "根据主键更新环境")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#request)", msClass = EnvironmentLogService.class)
    public Environment update(@Validated({Updated.class}) @RequestPart("request") EnvironmentRequest request,
                              @RequestPart(value = "file", required = false) List<MultipartFile> sslFiles) {
        return environmentService.update(request, SessionUtils.getUserId(), sslFiles);
    }

    /**
     * 查询所有环境。
     *
     * @return 所有数据
     */
    @PostMapping("list")
    @Operation(description = "查询所有环境")
    public List<Environment> list(@Validated @RequestBody EnvironmentFilterRequest request) {
        return environmentService.list(request);
    }

    /**
     * 根据主键获取环境。
     *
     * @param id 环境主键
     * @return 环境详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取环境")
    public EnvironmentInfoDTO getInfo(@PathVariable @Parameter(description = "环境主键") String id) {
        return environmentService.get(id);
    }

    /**
     * 分页查询环境。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询环境")
    public Page<Environment> page(@Parameter(description = "分页信息") Page<Environment> page) {
        return environmentService.page(page);
    }

    @PostMapping("/database/validate")
    @Operation(summary = "项目管理-环境-数据库配置-校验")
    // @RequiresPermissions(value = {PermissionConstants.PROJECT_ENVIRONMENT_READ, PermissionConstants.PROJECT_ENVIRONMENT_READ_ADD, PermissionConstants.PROJECT_ENVIRONMENT_READ_UPDATE}, logical = Logical.OR)
    public void validate(@Validated @RequestBody DataSource databaseConfig) {
        environmentService.validateDataSource(databaseConfig);
    }

    @GetMapping("/database/driver-options/{organizationId}")
    @Operation(summary = "项目管理-环境-数据库配置-数据库驱动选项")
    // @RequiresPermissions(value = {PermissionConstants.PROJECT_ENVIRONMENT_READ, PermissionConstants.PROJECT_ENVIRONMENT_READ_ADD, PermissionConstants.PROJECT_ENVIRONMENT_READ_UPDATE}, logical = Logical.OR)
    // @CheckOwner(resourceId = "#organizationId", resourceType = "organization")
    public List<OptionDTO> driverOptions(@PathVariable String organizationId) {
        return environmentService.getDriverOptions(organizationId);
    }
}
