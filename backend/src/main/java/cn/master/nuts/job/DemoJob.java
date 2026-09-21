package cn.master.nuts.job;


import cn.master.nuts.dto.system.TaskParameterConfig;
import cn.master.nuts.handler.schedule.BaseScheduleJob;
import cn.master.nuts.util.JSON;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
@Slf4j
public class DemoJob extends BaseScheduleJob {

    @Override
    protected void businessExecute(JobExecutionContext context) {
        log.info("=================================");
        System.out.println(runConfig);
        Map<String, Object> config = JSON.parseObject(runConfig, Map.class);
        config.forEach((key, value) -> log.info("DemoJob executed with config: {}", key));
        log.info("DemoJob executed with taskId: {}", taskId);
        log.info("=================================");
    }

    public static JobKey getJobKey(String resourceId) {
        return new JobKey(resourceId, DemoJob.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, DemoJob.class.getName());
    }
}
