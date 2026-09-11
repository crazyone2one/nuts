package cn.master.nuts.module.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.nuts.module.system.entity.Schedule;
import cn.master.nuts.module.system.mapper.ScheduleMapper;
import cn.master.nuts.module.system.service.ScheduleService;
import org.springframework.stereotype.Service;

/**
 * 定时任务 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-03
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule>  implements ScheduleService{

}
