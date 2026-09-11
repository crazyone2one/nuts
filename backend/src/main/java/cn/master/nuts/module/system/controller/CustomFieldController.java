package cn.master.nuts.module.system.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.nuts.module.system.entity.CustomField;
import cn.master.nuts.module.system.service.CustomFieldService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 自定义字段 控制层。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@RestController
@Tag(name = "自定义字段接口")
@RequestMapping("/customField")
public class CustomFieldController {

    @Autowired
    private CustomFieldService customFieldService;

    /**
     * 保存自定义字段。
     *
     * @param customField 自定义字段
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存自定义字段")
    public boolean save(@RequestBody @Parameter(description="自定义字段")CustomField customField) {
        return customFieldService.save(customField);
    }

    /**
     * 根据主键删除自定义字段。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除自定义字段")
    public boolean remove(@PathVariable @Parameter(description="自定义字段主键") String id) {
        return customFieldService.removeById(id);
    }

    /**
     * 根据主键更新自定义字段。
     *
     * @param customField 自定义字段
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新自定义字段")
    public boolean update(@RequestBody @Parameter(description="自定义字段主键") CustomField customField) {
        return customFieldService.updateById(customField);
    }

    /**
     * 查询所有自定义字段。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有自定义字段")
    public List<CustomField> list() {
        return customFieldService.list();
    }

    /**
     * 根据主键获取自定义字段。
     *
     * @param id 自定义字段主键
     * @return 自定义字段详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取自定义字段")
    public CustomField getInfo(@PathVariable @Parameter(description="自定义字段主键") String id) {
        return customFieldService.getById(id);
    }

    /**
     * 分页查询自定义字段。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询自定义字段")
    public Page<CustomField> page(@Parameter(description="分页信息") Page<CustomField> page) {
        return customFieldService.page(page);
    }

}
