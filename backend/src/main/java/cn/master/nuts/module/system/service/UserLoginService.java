package cn.master.nuts.module.system.service;

import cn.master.nuts.dto.system.UserDTO;
import cn.master.nuts.dto.user.UserRolePermissionDTO;
import cn.master.nuts.dto.user.UserRoleResourceDTO;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.module.system.entity.*;
import cn.master.nuts.module.system.mapper.UserMapper;
import cn.master.nuts.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.master.nuts.module.system.entity.table.ProjectTableDef.PROJECT;
import static cn.master.nuts.module.system.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;
import static cn.master.nuts.module.system.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.nuts.module.system.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.nuts.module.system.entity.table.UserTableDef.USER;

/**
 * @author : 11's papa
 * @since : 2026/9/14, 星期一
 **/
@Service
@RequiredArgsConstructor
public class UserLoginService {
    private final UserMapper userMapper;

    public UserDTO getUserDTO(String userId) {
        UserDTO userDTO = QueryChain.of(User.class).where(USER.ID.eq(userId)).oneAs(UserDTO.class);
        if (userDTO == null) {
            return null;
        }
        UserRolePermissionDTO dto = getUserRolePermission(userId);
        userDTO.setUserRoleRelations(dto.getUserRoleRelations());
        userDTO.setUserRoles(dto.getUserRoles());
        userDTO.setUserRolePermissions(dto.getList());
        return userDTO;
    }

    private UserRolePermissionDTO getUserRolePermission(String userId) {
        UserRolePermissionDTO permissionDTO = new UserRolePermissionDTO();
        List<UserRoleResourceDTO> list = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)).list();
        if (CollectionUtils.isEmpty(userRoleRelations)) {
            return permissionDTO;
        }
        permissionDTO.setUserRoleRelations(userRoleRelations);
        List<String> roleList = userRoleRelations.stream().map(UserRoleRelation::getRoleId).toList();
        List<UserRole> userRoles = QueryChain.of(UserRole.class).where(USER_ROLE.ID.in(roleList)).list();
        permissionDTO.setUserRoles(userRoles);
        for (UserRole gp : userRoles) {
            UserRoleResourceDTO dto = new UserRoleResourceDTO();
            dto.setUserRole(gp);
            List<UserRolePermission> userRolePermissions = QueryChain.of(UserRolePermission.class).where(USER_ROLE_PERMISSION.ROLE_ID.eq(gp.getId())).list();
            dto.setUserRolePermissions(userRolePermissions);
            list.add(dto);
        }
        permissionDTO.setList(list);
        return permissionDTO;
    }

    public boolean isSuperUser(String userId) {
        return QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq("admin")))
                .exists();
    }

    public void updateUser(User user) {
        if (StringUtils.isNotBlank(user.getEmail())) {
            boolean exists = QueryChain.of(User.class).where(USER.EMAIL.eq(user.getEmail())
                    .and(USER.ID.ne(user.getId()))).exists();
            if (exists) {
                throw new NSException(Translator.get("user_email_already_exists"));
            }
            User userFromDB = QueryChain.of(User.class).where(USER.ID.eq(user.getId())).one();
            if (user.getLastOrganizationId() != null && !Strings.CS.equals(user.getLastOrganizationId(), userFromDB.getLastOrganizationId())
                    && !isSuperUser(user.getId())) {
                List<Project> projects = getProjectListByWsAndUserId(user.getId(), user.getLastOrganizationId());
                if (!projects.isEmpty()) {
                    // 如果传入的 last_project_id 是 last_organization_id 下面的
                    boolean present = projects.stream().anyMatch(p -> Strings.CS.equals(p.getId(), user.getLastProjectId()));
                    if (!present) {
                        user.setLastProjectId(projects.getFirst().getId());
                    }
                } else {
                    user.setLastProjectId(StringUtils.EMPTY);
                }
            }
            userMapper.update(user);
        }
    }

    private List<Project> getProjectListByWsAndUserId(String userId, String organizationId) {
        List<Project> projects = QueryChain.of(Project.class).where(PROJECT.ORGANIZATION_ID.eq(organizationId).and(PROJECT.ENABLE.eq(true))).list();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)).list();
        List<Project> projectList = new ArrayList<>();
        userRoleRelations.forEach(userRoleRelation -> projects.forEach(project -> {
            if (Strings.CS.equals(userRoleRelation.getSourceId(), project.getId())) {
                if (!projectList.contains(project)) {
                    projectList.add(project);
                }
            }
        }));
        return projectList;
    }

    public List<Project> getEnableProjectListById(String id) {
        return QueryChain.of(Project.class).where(PROJECT.ID.eq(id).and(PROJECT.ENABLE.eq(true))).list();
    }

    public void switchUserResource(String wsId, UserDTO userDTO) {
        UserDTO user = getUserDTO(userDTO.getId());
        User newUser = new User();
        user.setLastOrganizationId(wsId);
        user.setLastProjectId(StringUtils.EMPTY);
        List<Project> projects = getProjectListByWsAndUserId(userDTO.getId(), wsId);
        if (CollectionUtils.isNotEmpty(projects)) {
            user.setLastProjectId(projects.getFirst().getId());
        }
        BeanUtils.copyProperties(newUser, user);
        userMapper.update(newUser);
    }
}
