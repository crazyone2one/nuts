package cn.master.nuts.handler.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
public abstract class BaseScheduleJob implements Job {
    protected String projectId;
    protected String taskId;
    protected String runConfig;

    protected abstract void businessExecute(JobExecutionContext context);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        this.projectId = jobDataMap.getString("projectId");
        this.taskId = jobDataMap.getString("taskId");
        this.runConfig = jobDataMap.getString("runConfig");
        businessExecute(context);
    }
}
