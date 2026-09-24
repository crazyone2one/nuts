package cn.master.nuts.dto.filemanagement;

import cn.master.nuts.module.project.entity.FileModule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author : 11's papa
 * @since : 2026/9/23, 星期三
 **/
@Data
@NoArgsConstructor
public class FileRepositoryLog {
    private String id;

    private String projectId;

    private String name;

    private String parentId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long pos;

    private String updateUser;

    private String createUser;

    private String moduleType;

    private String platform;

    private String url;

    private String token;

    private String userName;

    public FileRepositoryLog(FileModule module) {
        this.id = module.getId();
        this.projectId = module.getProjectId();
        this.name = module.getName();
        this.parentId = module.getParentId();
        this.createTime = module.getCreateTime();
        this.updateTime = module.getUpdateTime();
        this.pos = module.getPos();
        this.updateUser = module.getUpdateUser();
        this.createUser = module.getCreateUser();
        this.moduleType = module.getModuleType();
    }
}
