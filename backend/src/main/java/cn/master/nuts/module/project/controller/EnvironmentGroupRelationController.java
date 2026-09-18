package cn.master.nuts.module.project.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.nuts.module.project.entity.EnvironmentGroupRelation;
import cn.master.nuts.module.project.service.EnvironmentGroupRelationService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 环境组关联关系 控制层。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@RestController
@Tag(name = "环境组关联关系接口")
@RequestMapping("/environmentGroupRelation")
public class EnvironmentGroupRelationController {

    @Autowired
    private EnvironmentGroupRelationService environmentGroupRelationService;

    /**
     * 保存环境组关联关系。
     *
     * @param environmentGroupRelation 环境组关联关系
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存环境组关联关系")
    public boolean save(@RequestBody @Parameter(description="环境组关联关系")EnvironmentGroupRelation environmentGroupRelation) {
        return environmentGroupRelationService.save(environmentGroupRelation);
    }

    /**
     * 根据主键删除环境组关联关系。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除环境组关联关系")
    public boolean remove(@PathVariable @Parameter(description="环境组关联关系主键") String id) {
        return environmentGroupRelationService.removeById(id);
    }

    /**
     * 根据主键更新环境组关联关系。
     *
     * @param environmentGroupRelation 环境组关联关系
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新环境组关联关系")
    public boolean update(@RequestBody @Parameter(description="环境组关联关系主键") EnvironmentGroupRelation environmentGroupRelation) {
        return environmentGroupRelationService.updateById(environmentGroupRelation);
    }

    /**
     * 查询所有环境组关联关系。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有环境组关联关系")
    public List<EnvironmentGroupRelation> list() {
        return environmentGroupRelationService.list();
    }

    /**
     * 根据主键获取环境组关联关系。
     *
     * @param id 环境组关联关系主键
     * @return 环境组关联关系详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取环境组关联关系")
    public EnvironmentGroupRelation getInfo(@PathVariable @Parameter(description="环境组关联关系主键") String id) {
        return environmentGroupRelationService.getById(id);
    }

    /**
     * 分页查询环境组关联关系。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询环境组关联关系")
    public Page<EnvironmentGroupRelation> page(@Parameter(description="分页信息") Page<EnvironmentGroupRelation> page) {
        return environmentGroupRelationService.page(page);
    }

}
