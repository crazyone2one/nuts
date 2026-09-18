package cn.master.nuts.module.system.service;

import cn.master.nuts.handler.exception.NSException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Driver;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JdbcDriverPluginService {
    public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String DRIVER_OPTION_SEPARATOR = "&";
    public static final String SYSTEM_PLUGIN_ID = "system";

    public Driver getDriverByOptionId(String driverId) {
        String[] split = driverId.split(DRIVER_OPTION_SEPARATOR);
        String pluginId = split[0];
        String className = split[1];
        if (Strings.CS.equals(pluginId, SYSTEM_PLUGIN_ID)) {
            try {
                return (Driver) Class.forName(MYSQL_DRIVER_CLASS_NAME).getConstructor().newInstance();
            } catch (Exception e) {
                log.error("获取驱动失败", e);
                throw new NSException(e);
            }
        }
        // List<Driver> extensions = pluginLoadService.getMsPluginManager().getExtensions(Driver.class, pluginId);
        // return extensions.stream().filter(driver -> Strings.CS.equals(driver.getClass().getName(), className))
        //         .findFirst()
        //         .orElseThrow(() -> new NSException("未找到对应的驱动"));
        throw new NSException("未找到对应的驱动");
    }
}
