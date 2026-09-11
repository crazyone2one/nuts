package cn.master.nuts.module.auth;

/**
 * @author : 11's papa
 * @since : 2026/9/8, 星期二
 **/
public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super("Refresh token not found or expired");
    }
}
