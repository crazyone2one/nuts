package cn.master.nuts;

import cn.master.nuts.handler.jwt.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("cn.master.nuts.module.*.mapper")
@EnableConfigurationProperties({JwtProperties.class})
public class NutsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutsApplication.class, args);
    }

}
