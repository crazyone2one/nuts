package cn.master.nuts.module.project.service.impl;

import cn.master.nuts.dto.OptionDTO;
import cn.master.nuts.dto.environment.*;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.module.project.entity.Environment;
import cn.master.nuts.module.project.mapper.EnvironmentMapper;
import cn.master.nuts.module.project.service.EnvironmentService;
import cn.master.nuts.module.system.service.JdbcDriverPluginService;
import cn.master.nuts.util.JSON;
import cn.master.nuts.util.Translator;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Connection;
import java.sql.Driver;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static cn.master.nuts.module.project.entity.table.EnvironmentTableDef.ENVIRONMENT;

/**
 * 环境 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl extends ServiceImpl<EnvironmentMapper, Environment> implements EnvironmentService {
    private final JdbcDriverPluginService jdbcDriverPluginService;
    public static final long DEFAULT_NODE_INTERVAL_POS = 4096;
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final String PATH = "/project/environment/import";
    private static final String MOCK_EVN_SOCKET = "/mock-server/";
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String DRIVER_OPTION_SEPARATOR = "&";
    public static final String SYSTEM_PLUGIN_ID = "system";

    @Override
    public List<Environment> list(EnvironmentFilterRequest request) {
        return queryChain().select(ENVIRONMENT.ALL_COLUMNS).where(ENVIRONMENT.PROJECT_ID.eq(request.projectId()))
                .and(ENVIRONMENT.NAME.like(request.keyword()).or(ENVIRONMENT.ID.eq(request.keyword())))
                .orderBy(ENVIRONMENT.POS.desc())
                .list();
    }

    @Override
    public Environment add(EnvironmentRequest request, String userId, List<MultipartFile> sslFiles) {
        Environment environment = new Environment();
        environment.setCreateUser(userId);
        environment.setName(request.getName());
        environment.setProjectId(request.getProjectId());
        checkEnvironmentExist(environment);
        environment.setUpdateUser(userId);
        environment.setMock(false);
        environment.setDescription(request.getDescription());
        environment.setPos(getNextOrder(request.getProjectId()));
        environment.setConfig(JSON.toJSONBytes(request.getConfig()));
        mapper.insertSelective(environment);
        uploadFileToMinio(sslFiles, environment);
        return environment;
    }

    @Override
    public Environment update(EnvironmentRequest request, String userId, List<MultipartFile> sslFiles) {
        Environment environment = new Environment();
        environment.setId(request.getId());
        environment.setUpdateUser(userId);
        environment.setProjectId(request.getProjectId());
        environment.setName(request.getName());
        environment.setDescription(request.getDescription());
        checkEnvironmentExist(environment);
        if (Objects.nonNull(request.getConfig())) {
            environment.setConfig(JSON.toJSONBytes(request.getConfig()));
        }
        mapper.update(environment);
        uploadFileToMinio(sslFiles, environment);
        return environment;
    }

    @Override
    public void validateDataSource(DataSource databaseConfig) {
        try {
            Driver driver = jdbcDriverPluginService.getDriverByOptionId(databaseConfig.getDriverId());
            Properties properties = new Properties();
            properties.setProperty(USERNAME, databaseConfig.getUsername());
            properties.setProperty(PASSWORD, databaseConfig.getPassword());
            Connection connect = driver.connect(databaseConfig.getDbUrl(), properties);
            if (connect == null) {
                throw new NSException(Translator.get("api_test_environment_datasource_connect_failed"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        Environment environment = mapper.selectOneById(id);
        if (environment == null) {
            throw new NSException(Translator.get("api_test_environment_not_exist"));
        }
        if (BooleanUtils.isTrue(environment.getMock())) {
            throw new NSException(Translator.get("mock_environment_not_delete"));
        }
        mapper.delete(environment);
    }

    @Override
    public List<OptionDTO> getDriverOptions(String orgId) {
        return List.of(new OptionDTO(SYSTEM_PLUGIN_ID + DRIVER_OPTION_SEPARATOR + MYSQL_DRIVER_CLASS_NAME, MYSQL_DRIVER_CLASS_NAME));
    }

    @Override
    public EnvironmentInfoDTO get(String environmentId) {
        if (StringUtils.isBlank(environmentId)) {
            return null;
        }
        Environment environment = mapper.selectOneById(environmentId);
        if (environment == null) {
            return null;
        }
        EnvironmentInfoDTO environmentInfoDTO = new EnvironmentInfoDTO();
        BeanUtils.copyProperties(environment, environmentInfoDTO);
        environmentInfoDTO.setConfig(JSON.parseObject(new String(environment.getConfig()), EnvironmentConfig.class));
        return environmentInfoDTO;
    }

    private void uploadFileToMinio(List<MultipartFile> sslFiles, Environment environment) {

    }

    public long getNextOrder(String projectId) {
        Long pos = queryChain().select(ENVIRONMENT.POS).where(ENVIRONMENT.PROJECT_ID.eq(projectId)).orderBy(ENVIRONMENT.POS.desc())
                .limit(1).oneAs(Long.class);
        return (pos == null ? 0 : pos) + DEFAULT_NODE_INTERVAL_POS;
    }

    private void checkEnvironmentExist(Environment environment) {
        if (environment.getName() != null) {
            boolean exists = queryChain().where(ENVIRONMENT.NAME.eq(environment.getName())
                    .and(ENVIRONMENT.PROJECT_ID.eq(environment.getProjectId()))
                    .and(ENVIRONMENT.ID.ne(environment.getId()))).exists();
            if (exists) {
                throw new NSException(Translator.get("api_test_environment_already_exists"));
            }
        }
    }
}
