package cn.master.nuts.handler.schedule;

import cn.master.nuts.dto.system.TaskParameterConfig;
import cn.master.nuts.dto.system.TaskParameterItem;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.module.system.entity.Schedule;
import cn.master.nuts.util.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleManager {
    private final Scheduler scheduler;

    public void addCronJob(JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> jobClass, String cron, JobDataMap jobDataMap) {
        try {
            log.info("addCronJob: {},{}", triggerKey.getName(), triggerKey.getGroup());
            JobBuilder jobBuilder = JobBuilder.newJob(jobClass).withIdentity(jobKey);
            if (jobDataMap != null) {
                jobBuilder.usingJobData(jobDataMap);
            }

            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity(triggerKey);
            triggerBuilder.startNow();
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            scheduler.scheduleJob(jobBuilder.build(), trigger);
        } catch (Exception e) {
            log.error("addCronJob error: {}", e.getMessage(), e);
            throw new NSException("定时任务配置异常: " + e.getMessage());
        }
    }

    public void updateDataMap(JobKey jobKey, JobDataMap map) {
        try {
            log.info("updateDataMap: {},{}", jobKey.getName(), jobKey.getGroup());
            JobDetail currentJob = scheduler.getJobDetail(jobKey);
            if (currentJob == null) {
                throw new NSException("定时任务不存在: " + jobKey);
            }
            JobDetail updatedJob = currentJob.getJobBuilder()
                    .usingJobData(map)
                    .build();
            // Job 已经绑定 Trigger，更新时允许 Quartz 临时保存非 durable Job。
            scheduler.addJob(updatedJob, true, true);
        } catch (Exception e) {
            log.error("updateDataMap error: {}", e.getMessage(), e);
            if (e instanceof NSException nsException) {
                throw nsException;
            }
            throw new NSException("定时任务参数更新异常: " + e.getMessage());
        }
    }

    public void pauseJob(JobKey jobKey) {
        try {
            log.info("PauseJob: {},{}", jobKey.getName(), jobKey.getGroup());
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            log.error("PauseJob error: {}", e.getMessage(), e);
            throw new NSException("定时任务暂停异常: " + e.getMessage());
        }
    }

    public void resumeJob(JobKey jobKey) {
        try {
            log.info("ResumeJob: {},{}", jobKey.getName(), jobKey.getGroup());
            scheduler.resumeJob(jobKey);
        } catch (Exception e) {
            log.error("ResumeJob error: {}", e.getMessage(), e);
            throw new NSException("定时任务恢复异常: " + e.getMessage());
        }
    }

    public void removeJob(JobKey jobKey, TriggerKey triggerKey) {
        try {
            log.info("RemoveJob: {},{}", jobKey.getName(), jobKey.getGroup());
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            log.error("RemoveJob error: {}", e.getMessage(), e);
            throw new NSException("定时任务删除异常: " + e.getMessage());
        }
    }

    public void modifyCronJobTime(TriggerKey triggerKey, String cron) {
        log.info("modifyCronJobTime: {},{}", triggerKey.getName(), triggerKey.getGroup());
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }

            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                /* 方式一 ：调用 rescheduleJob 开始 */
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();// 触发器
                triggerBuilder.withIdentity(triggerKey);// 触发器名,触发器组
                triggerBuilder.startNow(); // 立即执行
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)); // 触发器时间设定
                trigger = (CronTrigger) triggerBuilder.build(); // 创建Trigger对象
                scheduler.rescheduleJob(triggerKey, trigger); // 修改一个任务的触发时间
            }
        } catch (Exception e) {
            log.error("modifyCronJobTime error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public JobDataMap getDefaultJobDataMap(Schedule schedule, String expression, String userId) {
        TaskParameterConfig taskParameterConfig = JSON.parseObject(schedule.getConfig(), TaskParameterConfig.class);
        Map<String, TaskParameterItem> parameters = taskParameterConfig.getParameters();

        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, TaskParameterItem> entry : parameters.entrySet()) {
            TaskParameterItem parameter = entry.getValue();
            if (Boolean.TRUE.equals(parameter.getEnabled())) {
                map.put(entry.getKey(), parameter.getValue());
            }
        }
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskId", schedule.getId());
        jobDataMap.put("expression", expression);
        jobDataMap.put("userId", userId);
        jobDataMap.put("config", schedule.getConfig());
        jobDataMap.put("projectId", schedule.getProjectId());
        jobDataMap.put("runConfig", JSON.toJSONString(map));
        return jobDataMap;
    }

    public void addOrUpdateCronJob(JobKey jobKey, TriggerKey triggerKey, Class clazz, String cronExpression, JobDataMap jobDataMap) throws SchedulerException {
        if (scheduler.checkExists(triggerKey)) {
            updateDataMap(jobKey, jobDataMap);
            modifyCronJobTime(triggerKey, cronExpression);
        } else {
            addCronJob(jobKey, triggerKey, clazz, cronExpression, jobDataMap);
        }
    }
}
