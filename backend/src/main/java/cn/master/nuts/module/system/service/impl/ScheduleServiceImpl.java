package cn.master.nuts.module.system.service.impl;

import cn.master.nuts.constants.ApplicationNumScope;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.handler.schedule.ScheduleManager;
import cn.master.nuts.handler.uid.NumGenerator;
import cn.master.nuts.module.system.entity.Schedule;
import cn.master.nuts.module.system.mapper.ScheduleMapper;
import cn.master.nuts.module.system.service.ScheduleService;
import cn.master.nuts.util.SessionUtils;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {
    private final ScheduleManager scheduleManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSchedule(Schedule schedule) {
        schedule.setNum(getNextNum(schedule.getProjectId()));
        schedule.setCreateUser(SessionUtils.getUserId());
        mapper.insertSelective(schedule);
    }

    @Override
    public void addOrUpdateCronJob(Schedule request, JobKey jobKey, TriggerKey triggerKey, Class clazz) {
        Boolean enable = request.getEnable();
        String cronExpression = request.getCronExpression();
        if (BooleanUtils.isTrue(enable) && StringUtils.isNotBlank(cronExpression)) {
            try {
                scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, clazz, cronExpression,
                        scheduleManager.getDefaultJobDataMap(request, cronExpression, request.getCreateUser()));
            } catch (SchedulerException e) {
                throw new NSException("定时任务开启异常: " + e.getMessage());
            }
        } else {
            try {
                scheduleManager.removeJob(jobKey, triggerKey);
            } catch (Exception e) {
                throw new NSException("定时任务关闭异常: " + e.getMessage());
            }
        }
    }

    @Override
    public void deleteByResourceId(String key, String job) {
        removeJob(key, job);
        mapper.deleteByQuery(queryChain().where(Schedule::getKey).eq(key));
    }

    private void removeJob(String key, String job) {
        scheduleManager.removeJob(new JobKey(key, job), new TriggerKey(key, job));
    }

    public long getNextNum(String projectId) {
        return NumGenerator.nextNum(projectId, ApplicationNumScope.TASK);
    }
}
