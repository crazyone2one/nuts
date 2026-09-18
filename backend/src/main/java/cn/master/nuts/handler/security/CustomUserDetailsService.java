package cn.master.nuts.handler.security;

import cn.master.nuts.module.system.entity.User;
import com.mybatisflex.core.query.QueryChain;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/3, 星期四
 **/
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = QueryChain.of(User.class).where(User::getName).eq(username).oneOpt()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
        return new CustomUserPrincipal(user, authorities);
    }
}
