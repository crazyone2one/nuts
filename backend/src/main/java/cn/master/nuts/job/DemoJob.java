package cn.master.nuts.job;


import cn.master.nuts.dto.JobParamDTO;
import cn.master.nuts.handler.schedule.BaseScheduleJob;
import cn.master.nuts.util.JSON;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
@Slf4j
public class DemoJob extends BaseScheduleJob {

    @Override
    protected void businessExecute(JobExecutionContext context) {
        log.info("=================================");
        JobParamDTO config = JSON.parseObject(this.config, JobParamDTO.class);
        log.info("DemoJob executed with config: {}", config.getBeginTime());
        log.info("DemoJob executed with config: {}", config.getEndTime());
        log.info("DemoJob executed with config: {}", config.getSensorCode());

        config.getExtra().forEach((key, value) -> log.info("DemoJob executed with config: {}", value));
        log.info("DemoJob executed with taskId: {}", taskId);
        log.info("=================================");

        // try{
        //     DataSourceKey.use("100001100001-ds");
        //     List<Row> rows = Db.selectAll("sf_risk_point");
        //     System.out.println(rows);
        // }finally{
        //     DataSourceKey.clear();
        // }
    }
}
