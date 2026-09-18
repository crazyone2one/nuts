package cn.master.nuts.util;

import cn.master.nuts.handler.security.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author : 11's papa
 * @since : 2026/9/11, 星期五
 **/
@Slf4j
public class SessionUtils {
    private static final ThreadLocal<String> projectId = new ThreadLocal<>();
    private static final ThreadLocal<String> organizationId = new ThreadLocal<>();

    public static CustomUserPrincipal getUser() {
        try {
            return (CustomUserPrincipal) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        } catch (Exception e) {
            log.warn("后台获取在线用户失败: {}", e.getMessage());
            return null;
        }
    }

    public static String getUserId() {
        CustomUserPrincipal user = getUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentOrganizationId() {
        if (StringUtils.isNotEmpty(organizationId.get())) {
            return organizationId.get();
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            log.debug("ORGANIZATION: {}", request.getHeader("ORGANIZATION"));
            if (request.getHeader("ORGANIZATION") != null) {
                return request.getHeader("ORGANIZATION");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return getUser().getLastOrganizationId();
    }

    public static void setCurrentOrganizationId(String organizationId) {
        SessionUtils.organizationId.set(organizationId);
    }

    public static void setCurrentProjectId(String projectId) {
        SessionUtils.projectId.set(projectId);
    }

    public static String getCurrentProjectId() {
        if (StringUtils.isNotEmpty(projectId.get())) {
            return projectId.get();
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            log.debug("PROJECT: {}", request.getHeader("PROJECT"));
            if (request.getHeader("PROJECT") != null) {
                return request.getHeader("PROJECT");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return getUser().getLastProjectId();
    }
}
