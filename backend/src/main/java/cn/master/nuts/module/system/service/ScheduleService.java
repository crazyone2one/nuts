package cn.master.nuts.module.system.service;

import cn.master.nuts.handler.validation.Created;
import cn.master.nuts.handler.validation.Updated;
import com.mybatisflex.core.service.IService;
import cn.master.nuts.module.system.entity.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * 定时任务 服务层。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
public interface ScheduleService extends IService<Schedule> {
    void addSchedule(Schedule schedule);

    void addOrUpdateCronJob(Schedule request, JobKey jobKey, TriggerKey triggerKey, Class clazz);

    void deleteByResourceId(String key, String job);
}
