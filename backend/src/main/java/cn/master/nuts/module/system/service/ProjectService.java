package cn.master.nuts.module.system.service;

import cn.master.nuts.dto.ProjectDTO;
import cn.master.nuts.dto.UserExtendDTO;
import cn.master.nuts.dto.system.*;
import cn.master.nuts.module.system.entity.Project;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 项目 服务层。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
public interface ProjectService extends IService<Project> {

    void add(AddProjectRequest request, String createUser);

    Page<ProjectDTO> page(ProjectRequest request);

    boolean delete(String id, String deleteUser);

    int updateProject(UpdateProjectRequest request, String userId);

    void enable(String id, String updateUser);

    void disable(String id, String updateUser);

    void rename(UpdateProjectNameRequest request, String userId);

    List<Project> getUserProject(String organizationId, String userId);

    UserDTO switchProject(ProjectSwitchRequest request, String currentUserId);

    List<Project> getUserProjectWidthModule(String organizationId, String module, String userId);

    List<UserExtendDTO> getMemberOption(String projectId, String keyword);
}
