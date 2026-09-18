package cn.master.nuts.module.project.service;

import cn.master.nuts.dto.OptionDTO;
import cn.master.nuts.dto.environment.DataSource;
import cn.master.nuts.dto.environment.EnvironmentFilterRequest;
import cn.master.nuts.dto.environment.EnvironmentInfoDTO;
import cn.master.nuts.dto.environment.EnvironmentRequest;
import com.mybatisflex.core.service.IService;
import cn.master.nuts.module.project.entity.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 环境 服务层。
 *
 * @author 11's papa
 * @since 2026-09-15
 */
public interface EnvironmentService extends IService<Environment> {

    List<Environment> list(EnvironmentFilterRequest request);

    Environment add(EnvironmentRequest request, String userId, List<MultipartFile> sslFiles);

    Environment update(EnvironmentRequest request, String userId, List<MultipartFile> sslFiles);

    void validateDataSource(DataSource databaseConfig);

    void delete(String id);

    List<OptionDTO> getDriverOptions(String orgId);

    EnvironmentInfoDTO get(String id);
}
