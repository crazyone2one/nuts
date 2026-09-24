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
import cn.master.nuts.module.project.entity.FileMetadata;
import cn.master.nuts.module.project.service.FileMetadataService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 文件基础信息 控制层。
 *
 * @author 11's papa
 * @since 2026-09-22
 */
@RestController
@Tag(name = "文件基础信息接口")
@RequestMapping("/fileMetadata")
public class FileMetadataController {

    @Autowired
    private FileMetadataService fileMetadataService;

    /**
     * 保存文件基础信息。
     *
     * @param fileMetadata 文件基础信息
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存文件基础信息")
    public boolean save(@RequestBody @Parameter(description="文件基础信息")FileMetadata fileMetadata) {
        return fileMetadataService.save(fileMetadata);
    }

    /**
     * 根据主键删除文件基础信息。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除文件基础信息")
    public boolean remove(@PathVariable @Parameter(description="文件基础信息主键") String id) {
        return fileMetadataService.removeById(id);
    }

    /**
     * 根据主键更新文件基础信息。
     *
     * @param fileMetadata 文件基础信息
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新文件基础信息")
    public boolean update(@RequestBody @Parameter(description="文件基础信息主键") FileMetadata fileMetadata) {
        return fileMetadataService.updateById(fileMetadata);
    }

    /**
     * 查询所有文件基础信息。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有文件基础信息")
    public List<FileMetadata> list() {
        return fileMetadataService.list();
    }

    /**
     * 根据主键获取文件基础信息。
     *
     * @param id 文件基础信息主键
     * @return 文件基础信息详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取文件基础信息")
    public FileMetadata getInfo(@PathVariable @Parameter(description="文件基础信息主键") String id) {
        return fileMetadataService.getById(id);
    }

    /**
     * 分页查询文件基础信息。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询文件基础信息")
    public Page<FileMetadata> page(@Parameter(description="分页信息") Page<FileMetadata> page) {
        return fileMetadataService.page(page);
    }

}
