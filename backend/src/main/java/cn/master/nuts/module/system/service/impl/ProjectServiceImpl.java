package cn.master.nuts.module.system.service.impl;

import cn.master.nuts.constants.HttpMethodConstants;
import cn.master.nuts.constants.InternalUserRole;
import cn.master.nuts.constants.OperationLogConstants;
import cn.master.nuts.dto.LogDTO;
import cn.master.nuts.dto.ProjectDTO;
import cn.master.nuts.dto.UserExtendDTO;
import cn.master.nuts.dto.system.*;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.module.log.constants.OperationLogModule;
import cn.master.nuts.module.log.constants.OperationLogType;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.entity.User;
import cn.master.nuts.module.system.entity.UserRole;
import cn.master.nuts.module.system.entity.UserRoleRelation;
import cn.master.nuts.module.system.mapper.OrganizationMapper;
import cn.master.nuts.module.system.mapper.ProjectMapper;
import cn.master.nuts.module.system.mapper.UserRoleRelationMapper;
import cn.master.nuts.module.system.service.OperationLogService;
import cn.master.nuts.module.system.service.ProjectService;
import cn.master.nuts.util.JSON;
import cn.master.nuts.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.nuts.module.system.entity.table.OrganizationTableDef.ORGANIZATION;
import static cn.master.nuts.module.system.entity.table.ProjectTableDef.PROJECT;
import static cn.master.nuts.module.system.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.nuts.module.system.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.nuts.module.system.entity.table.UserTableDef.USER;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    private final OperationLogService operationLogService;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final OrganizationMapper organizationMapper;

    private final static String PREFIX = "/system/project";
    private final static String ADD_PROJECT = PREFIX + "/add";
    private final static String UPDATE_PROJECT = PREFIX + "/update";
    private final static String REMOVE_PROJECT_MEMBER = PREFIX + "/remove-member/";
    private final static String ADD_MEMBER = PREFIX + "/add-member";

    @Override
    public void add(AddProjectRequest request, String createUser) {
        Project project = new Project();
        project.setName(request.getName());
        project.setNum(request.getNum());
        project.setOrganizationId(request.getOrganizationId());
        checkProjectExistByName(project);
        project.setUpdateUser(createUser);
        project.setCreateUser(createUser);
        project.setEnable(request.getEnable());
        project.setAllResourcePool(request.isAllResourcePool());
        project.setDescription(request.getDescription());
        mapper.insertSelective(project);

        ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
        memberRequest.setProjectIds(List.of(project.getId()));
        memberRequest.setUserIds(request.getUserIds());
        addProjectAdmin(memberRequest, createUser, ProjectServiceImpl.ADD_PROJECT, OperationLogType.ADD.name(), Translator.get("add"));
    }

    @Override
    public Page<ProjectDTO> page(ProjectRequest request) {
        Page<ProjectDTO> page = queryChain().select(PROJECT.ALL_COLUMNS)
                .select(ORGANIZATION.NAME.as("organizationName"))
                .from(PROJECT).innerJoin(ORGANIZATION).on(PROJECT.ORGANIZATION_ID.eq(ORGANIZATION.ID))
                .where(PROJECT.ORGANIZATION_ID.eq(request.getOrganizationId()))
                .and(PROJECT.NAME.like(request.getKeyword()).or(PROJECT.NAME.like(request.getKeyword())))
                .orderBy(PROJECT.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), ProjectDTO.class);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<String> projectIds = page.getRecords().stream().map(ProjectDTO::getId).toList();
            List<UserExtendDTO> users = getProjectAdminList(projectIds);
            List<ProjectDTO> projectDTOList = getProjectExtendDTOList(projectIds);
            Map<String, List<UserExtendDTO>> userMapList = users.stream().collect(Collectors.groupingBy(UserExtendDTO::getSourceId));
            Map<String, ProjectDTO> projectMap = projectDTOList.stream().collect(Collectors.toMap(ProjectDTO::getId, projectDTO -> projectDTO));
            page.getRecords().forEach(projectDTO -> {
                projectDTO.setMemberCount(projectMap.get(projectDTO.getId()).getMemberCount());
                List<UserExtendDTO> userExtendDTOS = userMapList.get(projectDTO.getId());
                if (CollectionUtils.isNotEmpty(userExtendDTOS)) {
                    projectDTO.setAdminList(userExtendDTOS);
                    List<String> userIdList = userExtendDTOS.stream().map(UserExtendDTO::getId).collect(Collectors.toList());
                    projectDTO.setProjectCreateUserIsAdmin(CollectionUtils.isNotEmpty(userIdList) && userIdList.contains(projectDTO.getCreateUser()));
                } else {
                    projectDTO.setAdminList(new ArrayList<>());
                }
            });
        }
        return page;
    }

    @Override
    public boolean delete(String id, String deleteUser) {
        checkProjectNotExist(id);
        return updateChain()
                .set(PROJECT.DELETE_TIME, LocalDateTime.now())
                .set(PROJECT.DELETED, true)
                .set(PROJECT.DELETE_USER, deleteUser).where(PROJECT.ID.eq(id)).update();

    }

    @Override
    public int updateProject(UpdateProjectRequest request, String updateUser) {
        Project project = new Project();
        BeanUtils.copyProperties(request, project);
        project.setUpdateUser(updateUser);
        checkProjectExistByName(project);
        checkProjectNotExist(project.getId());

        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getId())
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).list();
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        List<LogDTO> logDTOList = new ArrayList<>();
        List<String> deleteIds = orgUserIds.stream()
                .filter(item -> !request.getUserIds().contains(item))
                .toList();
        List<String> insertIds = request.getUserIds().stream()
                .filter(item -> !orgUserIds.contains(item))
                .toList();
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            QueryChain<UserRoleRelation> deleteExample = QueryChain.of(UserRoleRelation.class)
                    .where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getId())
                            .and(USER_ROLE_RELATION.USER_ID.in(deleteIds))
                            .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue())));
            deleteExample.list().forEach(userRoleRelation -> {
                User user = QueryChain.of(User.class).where(USER.ID.eq(userRoleRelation.getUserId())).one();
                String logProjectId = OperationLogConstants.SYSTEM;
                if (Strings.CS.equals(OperationLogModule.SETTING_SYSTEM_ORGANIZATION, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                    logProjectId = OperationLogConstants.ORGANIZATION;
                }
                LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), userRoleRelation.getId(), updateUser, OperationLogType.DELETE.name(), OperationLogModule.SETTING_SYSTEM_ORGANIZATION, Translator.get("delete") + Translator.get("project_admin") + ": " + user.getName());
                setLog(logDTO, ProjectServiceImpl.UPDATE_PROJECT, HttpMethodConstants.POST.name(), logDTOList);
            });
            userRoleRelationMapper.deleteByQuery(deleteExample);
        }
        if (CollectionUtils.isNotEmpty(insertIds)) {
            ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
            memberRequest.setProjectIds(List.of(project.getId()));
            memberRequest.setUserIds(insertIds);
            addProjectAdmin(memberRequest, updateUser, ProjectServiceImpl.UPDATE_PROJECT, OperationLogType.ADD.name(), Translator.get("add"));
        }
        if (CollectionUtils.isNotEmpty(logDTOList)) {
            operationLogService.batchAdd(logDTOList);
        }
        return mapper.update(project);
    }

    @Override
    public void enable(String id, String updateUser) {
        checkProjectNotExist(id);
        updateChain().set(PROJECT.ENABLE, true).set(PROJECT.UPDATE_USER, updateUser).where(PROJECT.ID.eq(id)).update();
    }

    @Override
    public void disable(String id, String updateUser) {
        checkProjectNotExist(id);
        updateChain().set(PROJECT.ENABLE, false).set(PROJECT.UPDATE_USER, updateUser).where(PROJECT.ID.eq(id)).update();
    }

    @Override
    public void rename(UpdateProjectNameRequest request, String userId) {
        checkProjectNotExist(request.id());
        Project project = new Project();
        project.setId(request.id());
        project.setName(request.name());
        project.setOrganizationId(request.organizationId());
        checkProjectExistByName(project);
        project.setUpdateUser(userId);
        mapper.update(project);
    }

    @Override
    public List<Project> getUserProject(String organizationId, String userId) {
        checkOrg(organizationId);
        User user = QueryChain.of(User.class).where(USER.ID.eq(userId)).one();
        String projectId;
        if (user != null && StringUtils.isNotBlank(user.getLastProjectId())) {
            projectId = user.getLastProjectId();
        } else {
            projectId = null;
        }
        List<Project> allProject;
        long count = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ADMIN.name()))).count();
        if (count > 0) {
            allProject = queryChain().select(PROJECT.ALL_COLUMNS)
                    .where(PROJECT.ORGANIZATION_ID.eq(organizationId).and(PROJECT.ENABLE.eq(true)))
                    .orderBy(PROJECT.NAME.asc())
                    .list();
        } else {
            allProject = queryChain().select(QueryMethods.distinct(PROJECT.ALL_COLUMNS))
                    .from(UserRole.class)
                    .join(UserRoleRelation.class).on(USER_ROLE.ID.eq(USER_ROLE_RELATION.ROLE_ID))
                    .join(PROJECT).on(PROJECT.ID.eq(USER_ROLE_RELATION.SOURCE_ID))
                    .join(USER).on(USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                    .where(USER_ROLE_RELATION.USER_ID.eq(userId).and(USER_ROLE.TYPE.eq("PROJECT"))
                            .and(PROJECT.ORGANIZATION_ID.eq(organizationId).and(PROJECT.ENABLE.eq(true))))
                    .orderBy(PROJECT.NAME.asc())
                    .list();
        }
        List<Project> temp = allProject;
        return allProject.stream()
                .filter(project -> Strings.CS.equals(project.getId(), projectId))
                .findFirst()
                .map(project -> {
                    temp.remove(project);
                    temp.add(0, project);
                    return temp;
                })
                .orElse(allProject);
    }

    @Override
    public UserDTO switchProject(ProjectSwitchRequest request, String currentUserId) {
        if (!Strings.CS.equals(currentUserId, request.userId())) {
            throw new NSException(Translator.get("not_authorized"));
        }
        if (mapper.selectOneById(request.projectId()) == null) {
            throw new NSException(Translator.get("project_not_exist"));
        }
        return null;
    }

    @Override
    public List<Project> getUserProjectWidthModule(String organizationId, String module, String userId) {
        if (StringUtils.isBlank(module)) {
            throw new NSException(Translator.get("module.name.is.empty"));
        }
        return List.of();
    }

    @Override
    public List<UserExtendDTO> getMemberOption(String projectId, String keyword) {
        if (mapper.selectOneById(projectId) == null) {
            return List.of();
        }
        return QueryChain.of(User.class)
                .select(QueryMethods.distinct(USER.ALL_COLUMNS))
                .from(USER_ROLE_RELATION).join(USER).on(USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                .where(USER.ENABLE.eq(true).and(USER.NAME.like(keyword).or(USER.EMAIL.like(keyword))))
                .orderBy(USER.NAME.asc()).limit(1000)
                .listAs(UserExtendDTO.class);
    }

    private void checkOrg(String organizationId) {
        if (organizationMapper.selectOneById(organizationId) == null) {
            throw new NSException(Translator.get("organization_not_exist"));
        }
    }

    private List<ProjectDTO> getProjectExtendDTOList(List<String> projectIds) {
        QueryChain<UserRoleRelation> tempQuery = QueryChain.of(UserRoleRelation.class)
                .select(USER_ROLE_RELATION.SOURCE_ID, USER.ID)
                .from(USER_ROLE_RELATION).leftJoin(USER).on(USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds));
        return queryChain().select(PROJECT.ID).select("count(distinct temp.id) as memberCount")
                .from(PROJECT).as("p")
                .leftJoin(tempQuery).as("temp").on("p.id = temp.source_id")
                .groupBy(PROJECT.ID)
                .listAs(ProjectDTO.class);
    }

    private List<UserExtendDTO> getProjectAdminList(List<String> projectIds) {
        return QueryChain.of(UserRoleRelation.class)
                .select(USER.ALL_COLUMNS, USER_ROLE_RELATION.SOURCE_ID)
                .from(USER_ROLE_RELATION).leftJoin(USER).on(USER_ROLE_RELATION.USER_ID.eq(USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds))
                .and(USER_ROLE_RELATION.ROLE_ID.eq("project_admin")).listAs(UserExtendDTO.class);
    }

    private void addProjectAdmin(ProjectAddMemberBatchRequest request, String createUser, String path, String type, String content) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        request.getProjectIds().forEach(projectId -> {
            Project project = queryChain().where(PROJECT.ID.eq(projectId)).one();
            Map<String, String> nameMap = addUserPre(request.getUserIds(), createUser, path, projectId, project);
            request.getUserIds().forEach(userId -> {
                QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                        .where(USER_ROLE_RELATION.USER_ID.eq(userId)
                                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))
                                .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId)));
                if (!userRoleRelationQueryChain.exists()) {
                    UserRoleRelation adminRole = new UserRoleRelation();
                    adminRole.setUserId(userId);
                    adminRole.setRoleId(InternalUserRole.PROJECT_ADMIN.getValue());
                    adminRole.setSourceId(projectId);
                    adminRole.setCreateUser(createUser);
                    adminRole.setOrganizationId(project.getOrganizationId());
                    userRoleRelations.add(adminRole);
                    String logProjectId = OperationLogConstants.SYSTEM;
                    if (Strings.CS.equals(OperationLogModule.SETTING_SYSTEM_ORGANIZATION, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                        logProjectId = OperationLogConstants.ORGANIZATION;
                    }
                    LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), adminRole.getId(), createUser, type, OperationLogModule.SETTING_SYSTEM_ORGANIZATION, content + Translator.get("project_admin") + ": " + nameMap.get(userId));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
        });
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
        operationLogService.batchAdd(logDTOList);
    }

    private Map<String, String> addUserPre(List<String> userIds, String createUser, String path, String projectId, Project project) {
        checkProjectNotExist(projectId);
        List<User> users = QueryChain.of(User.class).where(User::getId).in(userIds).list();
        if (userIds.size() != users.size()) {
            throw new NSException(Translator.get("user_not_exist"));
        }
        // 把id和名称放一个map中
        Map<String, String> userMap = users.stream().collect(Collectors.toMap(User::getId, User::getName));
        checkOrgRoleExit(userIds, project.getOrganizationId(), createUser, userMap, path);
        return userMap;
    }

    private void checkOrgRoleExit(List<String> userId, String orgId, String createUser, Map<String, String> nameMap, String path) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.in(userId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(orgId))).list();
        // 把用户id放到一个新的list
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        if (CollectionUtils.isNotEmpty(userId)) {
            List<UserRoleRelation> userRoleRelation = new ArrayList<>();
            userId.forEach(id -> {
                if (!orgUserIds.contains(id)) {
                    UserRoleRelation memberRole = new UserRoleRelation();
                    memberRole.setUserId(id);
                    memberRole.setRoleId(InternalUserRole.ORG_MEMBER.getValue());
                    memberRole.setSourceId(orgId);
                    memberRole.setCreateUser(createUser);
                    memberRole.setOrganizationId(orgId);
                    userRoleRelation.add(memberRole);
                    LogDTO logDTO = new LogDTO(orgId, orgId, memberRole.getId(), createUser, OperationLogType.ADD.name(), OperationLogModule.SETTING_SYSTEM_ORGANIZATION, Translator.get("add") + Translator.get("organization_member") + ": " + nameMap.get(id));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
            if (CollectionUtils.isNotEmpty(userRoleRelation)) {
                userRoleRelationMapper.insertBatch(userRoleRelation);
            }
        }
        operationLogService.batchAdd(logDTOList);
    }

    private void checkProjectNotExist(String projectId) {
        if (mapper.selectOneById(projectId) == null) {
            throw new NSException(Translator.get("project_is_not_exist"));
        }
    }

    public void setLog(LogDTO dto, String path, String method, List<LogDTO> logDTOList) {
        dto.setPath(path);
        dto.setMethod(method);
        dto.setOriginalValue(JSON.toJSONBytes(StringUtils.EMPTY));
        logDTOList.add(dto);
    }

    private void checkProjectExistByName(Project project) {
        QueryChain<Project> queryChain = queryChain().where(PROJECT.NAME.eq(project.getName())
                .and(PROJECT.ORGANIZATION_ID.eq(project.getOrganizationId()))
                .and(PROJECT.NUM.eq(project.getNum()))
                .and(PROJECT.ID.ne(project.getId())));
        if (queryChain.exists()) {
            throw new NSException(Translator.get("project_name_already_exists"));
        }
    }
}
