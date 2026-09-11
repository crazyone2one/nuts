package cn.master.nuts;

import cn.master.nuts.module.system.entity.User;
import cn.master.nuts.module.system.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootTest
class NutsApplicationTests {
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    UserMapper userMapper;
    @Test
    void contextLoads() {
        byte[] randomBytes = new byte[48];
        new SecureRandom().nextBytes(randomBytes);
        System.out.println(Base64.getEncoder().encodeToString(randomBytes));
    }

}
