package cn.master.nuts.module.system.controller;

import cn.master.nuts.handler.result.Views;
import cn.master.nuts.module.system.entity.User;
import cn.master.nuts.module.system.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@RestController
@Tag(name = "用户接口")
@RequiredArgsConstructor
@RequestMapping("/system/user")
public class UserController {

    private final UserService userService;

    /**
     * 保存用户。
     *
     * @param user 用户
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description = "保存用户")
    public boolean save(@RequestBody @Parameter(description = "用户") User user) {
        return userService.save(user);
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除用户")
    public boolean remove(@PathVariable @Parameter(description = "用户主键") String id) {
        return userService.removeById(id);
    }

    /**
     * 根据主键更新用户。
     *
     * @param user 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新用户")
    public boolean update(@RequestBody @Parameter(description = "用户主键") User user) {
        return userService.updateById(user);
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @JsonView(Views.Public.class)
    @Operation(description = "查询所有用户")
    public List<User> list() {
        return userService.list();
    }

    /**
     * 根据主键获取用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取用户")
    public User getInfo(@PathVariable @Parameter(description = "用户主键") String id) {
        return userService.getById(id);
    }

    /**
     * 分页查询用户。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询用户")
    @JsonView(Views.Public.class)
    public Page<User> page(@Parameter(description = "分页信息") Page<User> page) {
        return userService.page(page);
    }

}
