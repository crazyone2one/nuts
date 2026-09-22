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
    protected String projectNum;
    protected String taskId;
    protected String config;
    // 文件协议类型-- 0-国家局 1-省局 2-自治区 3-other
    protected String fileProtocolType;

    protected abstract void businessExecute(JobExecutionContext context);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        this.projectId = jobDataMap.getString("projectId");
        this.projectNum = jobDataMap.getString("projectNum");
        this.taskId = jobDataMap.getString("taskId");
        this.config = jobDataMap.getString("config");
        this.fileProtocolType = jobDataMap.getString("fileProtocolType");
        businessExecute(context);
    }
}
