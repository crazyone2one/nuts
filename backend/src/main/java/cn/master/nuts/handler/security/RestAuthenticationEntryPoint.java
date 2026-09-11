package cn.master.nuts.handler.security;

import cn.master.nuts.handler.result.NsHttpResultCode;
import cn.master.nuts.handler.result.ResultHolder;
import cn.master.nuts.util.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * @author : 11's papa
 * @since : 2026/9/8, 星期二
 **/
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ResultHolder error = ResultHolder.error(NsHttpResultCode.UNAUTHORIZED.getCode(), NsHttpResultCode.UNAUTHORIZED.getMessage(), authException.getMessage());
        response.getWriter().write(JSON.toJSONString(error));
    }
}
