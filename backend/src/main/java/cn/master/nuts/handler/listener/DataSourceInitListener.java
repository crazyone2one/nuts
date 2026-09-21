package cn.master.nuts.handler.listener;

import cn.master.nuts.dto.environment.DataSource;
import cn.master.nuts.dto.environment.EnvironmentConfig;
import cn.master.nuts.module.project.entity.Environment;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.util.JSON;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.query.QueryChain;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
@Slf4j
@Component
public class DataSourceInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        log.info("初始化数据源");
        FlexDataSource dataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        List<Project> list = QueryChain.of(Project.class).where(Project::getEnable).eq(true).list();
        if (CollectionUtils.isNotEmpty(list)) {
            List<Environment> environmentList = QueryChain.of(Environment.class).where(Environment::getProjectId).in(list.stream().map(Project::getId).toList()).list();
            if (CollectionUtils.isNotEmpty(environmentList)) {
                for (Environment environment : environmentList) {
                    EnvironmentConfig environmentConfig = JSON.parseObject(new String(environment.getConfig()), EnvironmentConfig.class);
                    DataSource dataSource1 = environmentConfig.getDataSources().getFirst();
                    HikariConfig hikariConfig = new HikariConfig();
                    hikariConfig.setDriverClassName(dataSource1.getDriver().split("&")[1]);
                    hikariConfig.setJdbcUrl(dataSource1.getDbUrl());
                    hikariConfig.setUsername(dataSource1.getUsername());
                    hikariConfig.setPassword(dataSource1.getPassword());
                    HikariDataSource newDataSource = new HikariDataSource(hikariConfig);
                    dataSource.addDataSource(dataSource1.getDataSource(), newDataSource);
                }
            }
        }
    }
}
