package cn.master.nuts.job;


import cn.master.nuts.handler.schedule.BaseScheduleJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
public class DemoJob extends BaseScheduleJob {

    @Override
    protected void businessExecute(JobExecutionContext context) {
        System.out.println("DemoJob");
    }

    public static JobKey getJobKey(String resourceId) {
        return new JobKey(resourceId, DemoJob.class.getName());
    }

    public static TriggerKey getTriggerKey(String resourceId) {
        return new TriggerKey(resourceId, DemoJob.class.getName());
    }
}
