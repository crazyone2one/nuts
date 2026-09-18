package cn.master.nuts.handler.security;

import cn.master.nuts.module.system.entity.User;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author : 11's papa
 * @since : 2026/9/11, 星期五
 **/
public class CustomUserPrincipal implements UserDetails {
    @Getter
    private final String userId;
    @Getter
    private final String lastOrganizationId;
    @Getter
    private final String lastProjectId;
    private final Collection<? extends GrantedAuthority> authorities;
    private final User user;


    public CustomUserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        this.userId = user.getId();
        this.user = user;
        this.authorities = authorities;
        this.lastOrganizationId = user.getLastOrganizationId();
        this.lastProjectId = user.getLastProjectId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }
}
