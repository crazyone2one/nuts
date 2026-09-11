package cn.master.nuts.module;

import cn.master.nuts.dto.LoginRequest;
import cn.master.nuts.dto.system.UserDTO;
import cn.master.nuts.handler.result.ResultHolder;
import cn.master.nuts.handler.result.Views;
import cn.master.nuts.module.auth.AuthService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 11's papa
 * @since : 2026/9/3, 星期四
 **/
@RestController
@RequiredArgsConstructor
public class LoginController {
    private final AuthService authService;

    @PostMapping("/login")
    @JsonView(Views.Internal.class)
    public UserDTO login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // ...
        return authService.login(loginRequest, response);
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest req, HttpServletResponse res) {
        return authService.refreshToken(req, res);
    }

    @PostMapping("/logout")
    public ResultHolder logout(HttpServletRequest req, HttpServletResponse res) {
        return authService.logout(req, res);
    }
}
