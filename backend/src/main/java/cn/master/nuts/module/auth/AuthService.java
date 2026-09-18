package cn.master.nuts.module.auth;

import cn.master.nuts.constants.UserRoleType;
import cn.master.nuts.dto.LoginRequest;
import cn.master.nuts.dto.system.UserDTO;
import cn.master.nuts.handler.jwt.JwtTokenProvider;
import cn.master.nuts.handler.result.ResultHolder;
import cn.master.nuts.handler.security.CustomUserDetailsService;
import cn.master.nuts.handler.security.CustomUserPrincipal;
import cn.master.nuts.module.system.entity.Organization;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.entity.UserRole;
import cn.master.nuts.module.system.entity.UserRoleRelation;
import cn.master.nuts.module.system.service.UserLoginService;
import com.mybatisflex.core.query.QueryChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static cn.master.nuts.module.system.entity.table.OrganizationTableDef.ORGANIZATION;
import static cn.master.nuts.module.system.entity.table.ProjectTableDef.PROJECT;

/**
 * @author : 11's papa
 * @since : 2026/9/4, 星期五
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserLoginService userLoginService;


    public UserDTO login(LoginRequest loginRequest, HttpServletResponse res) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        context.setAuthentication(authenticationResponse);
        SecurityContextHolder.setContext(context);
        CustomUserPrincipal userDetails = (CustomUserPrincipal) authenticationResponse.getPrincipal();
        assert userDetails != null;
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());
        setRefreshCookie(res, refreshToken);

        UserDTO userDTO = userLoginService.getUserDTO(userDetails.getUserId());
        userDTO.setAccessToken(accessToken);
        autoSwitch(userDTO);
        return userDTO;
    }

    private void autoSwitch(UserDTO user) {
        if (isSystemAdmin(user)) {
            return;
        }
        // 用户有 last_project_id 权限
        if (hasLastProjectPermission(user)) {
            return;
        }
        // 用户有 last_organization_id 权限
        if (hasLastOrganizationPermission(user)) {
            return;
        }
        // 判断其他权限
        checkNewOrganizationAndProject(user);
    }

    private void checkNewOrganizationAndProject(UserDTO user) {
        List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations();
        List<String> projectRoleIds = user.getUserRoles()
                .stream().filter(ug -> Strings.CS.equals(ug.getType(), UserRoleType.PROJECT.name()))
                .map(UserRole::getId)
                .toList();
        List<UserRoleRelation> project = userRoleRelations.stream().filter(ug -> projectRoleIds.contains(ug.getRoleId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(project)) {
            List<String> organizationIds = user.getUserRoles()
                    .stream()
                    .filter(ug -> Strings.CS.equals(ug.getType(), UserRoleType.ORGANIZATION.name()))
                    .map(UserRole::getId)
                    .toList();
            List<UserRoleRelation> organizations = userRoleRelations.stream().filter(ug -> organizationIds.contains(ug.getRoleId()))
                    .toList();
            if (CollectionUtils.isNotEmpty(organizations)) {
                // 获取所有的组织
                List<String> orgIds = organizations.stream().map(UserRoleRelation::getSourceId).collect(Collectors.toList());
                List<Organization> organizationsList = QueryChain.of(Organization.class).where(ORGANIZATION.ID.in(orgIds).and(ORGANIZATION.ENABLE.eq(true))).list();
                if (CollectionUtils.isNotEmpty(organizationsList)) {
                    String wsId = organizationsList.getFirst().getId();
                    userLoginService.switchUserResource(wsId, user);
                }
            } else {
                // 用户登录之后没有项目和组织的权限就把值清空
                user.setLastOrganizationId(StringUtils.EMPTY);
                user.setLastProjectId(StringUtils.EMPTY);
                userLoginService.updateUser(user);
            }
        } else {
            UserRoleRelation userRoleRelation = project.stream().filter(p -> StringUtils.isNotBlank(p.getSourceId()))
                    .toList().getFirst();
            String projectId = userRoleRelation.getSourceId();
            Project p = userLoginService.getEnableProjectListById(projectId).getFirst();
            String wsId = p.getOrganizationId();
            user.setId(user.getId());
            user.setLastProjectId(projectId);
            user.setLastOrganizationId(wsId);
            userLoginService.updateUser(user);
        }
    }

    private boolean hasLastOrganizationPermission(UserDTO user) {
        if (StringUtils.isNotBlank(user.getLastOrganizationId())) {
            List<Organization> organizations = QueryChain.of(Organization.class).where(ORGANIZATION.ID.eq(user.getLastOrganizationId()).and(ORGANIZATION.ENABLE.eq(true))).list();
            if (CollectionUtils.isEmpty(organizations)) {
                return false;
            }
            List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations().stream()
                    .filter(ug -> Strings.CS.equals(user.getLastOrganizationId(), ug.getSourceId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userRoleRelations)) {
                List<Project> projects = QueryChain.of(Project.class).where(PROJECT.ORGANIZATION_ID.eq(user.getLastOrganizationId()).and(PROJECT.ENABLE.eq(true))).list();
                // 组织下没有项目
                if (CollectionUtils.isEmpty(projects)) {
                    user.setLastProjectId(StringUtils.EMPTY);
                    userLoginService.updateUser(user);
                    return true;
                }
                // 组织下有项目，选中有权限的项目
                List<String> projectIds = projects.stream()
                        .map(Project::getId)
                        .toList();

                List<UserRoleRelation> roleRelations = user.getUserRoleRelations();
                List<String> projectRoleIds = user.getUserRoles()
                        .stream().filter(ug -> Strings.CS.equals(ug.getType(), UserRoleType.PROJECT.name()))
                        .map(UserRole::getId)
                        .toList();
                List<String> projectIdsWithPermission = roleRelations.stream().filter(ug -> projectRoleIds.contains(ug.getRoleId()))
                        .map(UserRoleRelation::getSourceId)
                        .filter(StringUtils::isNotBlank)
                        .filter(projectIds::contains)
                        .toList();

                List<String> intersection = projectIds.stream().filter(projectIdsWithPermission::contains).collect(Collectors.toList());
                // 当前组织下的所有项目都没有权限
                if (CollectionUtils.isEmpty(intersection)) {
                    user.setLastProjectId(StringUtils.EMPTY);
                    userLoginService.updateUser(user);
                    return true;
                }
                Optional<Project> first = projects.stream().filter(p -> Strings.CS.equals(intersection.getFirst(), p.getId())).findFirst();
                if (first.isPresent()) {
                    Project project = first.get();
                    String wsId = project.getOrganizationId();
                    user.setId(user.getId());
                    user.setLastProjectId(project.getId());
                    user.setLastOrganizationId(wsId);
                    userLoginService.updateUser(user);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasLastProjectPermission(UserDTO user) {
        if (StringUtils.isNotBlank(user.getLastProjectId())) {
            List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations().stream()
                    .filter(ug -> Strings.CS.equals(user.getLastProjectId(), ug.getSourceId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userRoleRelations)) {
                List<Project> projects = userLoginService.getEnableProjectListById(user.getLastProjectId());
                if (CollectionUtils.isNotEmpty(projects)) {
                    Project project = projects.getFirst();
                    if (Strings.CS.equals(project.getOrganizationId(), user.getLastOrganizationId())) {
                        return true;
                    }
                    user.setLastOrganizationId(project.getOrganizationId());
                    userLoginService.updateUser(user);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSystemAdmin(UserDTO user) {
        if (userLoginService.isSuperUser(user.getId())) {
            if (StringUtils.isNotBlank(user.getLastProjectId())) {
                List<Project> projects = userLoginService.getEnableProjectListById(user.getLastProjectId());
                if (CollectionUtils.isNotEmpty(projects)) {
                    Project project = projects.getFirst();
                    if (Strings.CS.equals(project.getOrganizationId(), user.getLastOrganizationId())) {
                        return true;
                    }
                    user.setLastOrganizationId(project.getOrganizationId());
                    userLoginService.updateUser(user);
                    return true;
                }
            }
            // 项目没有权限  则取当前组织下的第一个项目
            if (StringUtils.isNotBlank(user.getLastOrganizationId())) {
                List<Organization> organizations = QueryChain.of(Organization.class).where(ORGANIZATION.ID.eq(user.getLastOrganizationId()).and(ORGANIZATION.ENABLE.eq(true))).list();
                if (CollectionUtils.isNotEmpty(organizations)) {
                    Organization organization = organizations.getFirst();
                    List<Project> projectList = QueryChain.of(Project.class).where(PROJECT.ORGANIZATION_ID.eq(organization.getId()).and(PROJECT.ENABLE.eq(true))).list();
                    if (CollectionUtils.isNotEmpty(projectList)) {
                        Project project = projectList.getFirst();
                        user.setLastProjectId(project.getId());
                        userLoginService.updateUser(user);
                        return true;
                    } else {
                        // 组织下无项目, 走前端逻辑, 跳转到无项目的路由
                        userLoginService.updateUser(user);
                        return true;
                    }
                }
            }
            // 项目和组织都没有权限
            Project project = QueryChain.of(Project.class).from(PROJECT)
                    .leftJoin(ORGANIZATION).on(PROJECT.ORGANIZATION_ID.eq(ORGANIZATION.ID))
                    .where(PROJECT.ENABLE.eq(true).and(ORGANIZATION.ENABLE.eq(true))).limit(1).one();
            if (project != null) {
                user.setLastProjectId(project.getId());
                user.setLastOrganizationId(project.getOrganizationId());
                userLoginService.updateUser(user);
                return true;
            }
            return true;
        }
        return false;
    }

    private void setRefreshCookie(HttpServletResponse res, String token) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict") // 强烈建议添加，防御 CSRF
                .path("/")   // 限制 Cookie 仅在刷新接口发送
                .maxAge(Duration.ofDays(1))
                .build();

        // 仅通过 Set-Cookie 下发，绝不在响应头或响应体中返回 Token
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest req, HttpServletResponse res) {
        String rt = extractRefreshToken(req);
        // 场景①：cookie 缺失
        if (Objects.isNull(rt)) {
            clearRefreshCookie(res); // 兜底清一次，避免残留过期 cookie
            return ResponseEntity.status(401).body(Map.of(
                    "code", "REFRESH_TOKEN_MISSING",
                    "message", "未找到刷新令牌"));
        }
        try {
            JwtTokenProvider.RefreshResult result = jwtTokenProvider.rotateRefreshToken(rt);
            UserDetails ud = userDetailsService.loadUserByUsername(result.username());
            setRefreshCookie(res, result.newRefreshToken());
            return ResponseEntity.ok(Map.of("accessToken", jwtTokenProvider.generateAccessToken(ud)));
        } catch (RefreshTokenReuseException e) {
            // 场景③：安全事件——必须告警/埋点，不能只静默 401
            log.error("REFRESH TOKEN REUSE DETECTED, family revoked, remote={}",
                    req.getRemoteAddr(), e);
            clearRefreshCookie(res);
            return ResponseEntity.status(401).body(Map.of("code", "REFRESH_TOKEN_REUSED"));
        } catch (RefreshTokenNotFoundException e) {
            // 场景②：会话已失效（过期/被清库/伪造），正常登出流程
            log.info("Refresh token not found: {}", e.getMessage());
            clearRefreshCookie(res);
            return ResponseEntity.status(401).body(Map.of("code", "REFRESH_TOKEN_INVALID"));
        }
    }


    private String extractRefreshToken(HttpServletRequest req) {
        if (req.getCookies() == null) {
            return null;
        }
        return Arrays.stream(req.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public ResultHolder logout(HttpServletRequest request, HttpServletResponse response) {
        String rt = extractRefreshToken(request);
        if (rt != null) {
            jwtTokenProvider.revokeToken(rt);
        }
        clearRefreshCookie(response);
        return ResultHolder.success("logout successful");
    }

    private void clearRefreshCookie(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // 立即过期，实现清除
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
