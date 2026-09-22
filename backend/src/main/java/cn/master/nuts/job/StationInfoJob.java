package cn.master.nuts.job;

import cn.master.nuts.handler.schedule.BaseScheduleJob;
import org.quartz.JobExecutionContext;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
public class StationInfoJob extends BaseScheduleJob {
    @Override
    protected void businessExecute(JobExecutionContext context) {
        String fileName = projectNum + "_FZJC_";
    }
}
