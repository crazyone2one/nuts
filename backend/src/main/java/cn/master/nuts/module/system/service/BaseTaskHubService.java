package cn.master.nuts.module.system.service;

import cn.master.nuts.constants.HttpMethodConstants;
import cn.master.nuts.dto.BasePageRequest;
import cn.master.nuts.dto.LogDTO;
import cn.master.nuts.dto.LogDTOBuilder;
import cn.master.nuts.dto.system.*;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.handler.schedule.ScheduleManager;
import cn.master.nuts.module.log.constants.OperationLogType;
import cn.master.nuts.module.system.entity.Project;
import cn.master.nuts.module.system.entity.Schedule;
import cn.master.nuts.util.JSON;
import cn.master.nuts.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.nuts.module.system.entity.table.ProjectTableDef.PROJECT;
import static cn.master.nuts.module.system.entity.table.ScheduleTableDef.SCHEDULE;

/**
 * @author : 11's papa
 * @since : 2026/9/20, 星期日
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseTaskHubService {
    private final ScheduleService scheduleService;
    private final OperationLogService operationLogService;
    private final ScheduleManager scheduleManager;

    public Page<TaskHubScheduleDTO> getScheduleTaskList(BasePageRequest request, List<String> projectIds) {
        return QueryChain.of(Schedule.class)
                .select(SCHEDULE.ALL_COLUMNS)
                .select("QRTZ_TRIGGERS.PREV_FIRE_TIME AS last_time")
                .select("QRTZ_TRIGGERS.NEXT_FIRE_TIME AS nextTime")
                .from(SCHEDULE)
                .leftJoin(PROJECT).on(SCHEDULE.PROJECT_ID.eq(PROJECT.ID))
                .leftJoin("QRTZ_TRIGGERS").on("QRTZ_TRIGGERS.TRIGGER_NAME = schedule.key")
                .where(SCHEDULE.NAME.like(request.getKeyword()).or(SCHEDULE.NUM.like(request.getKeyword()))).and(SCHEDULE.PROJECT_ID.in(projectIds))
                .orderBy(SCHEDULE.CREATE_TIME.desc(), SCHEDULE.ENABLE.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), TaskHubScheduleDTO.class);
    }

    public void enable(String id, String userId, String path, String module) {
        Schedule schedule = checkScheduleExit(id);
        // UpdateChain.of(Schedule.class).set(SCHEDULE.ENABLE, !schedule.getEnable()).where(SCHEDULE.ID.eq(id)).update();
        schedule.setEnable(!schedule.getEnable());
        updateTaskAndSchedule(schedule);
        saveLog(List.of(schedule), userId, path, HttpMethodConstants.GET.name(), module, OperationLogType.UPDATE.name());
    }

    public void deleteScheduleTask(String id, String userId, String path, String module) {
        Schedule schedule = checkScheduleExit(id);
        scheduleService.deleteByResourceId(schedule.getKey(), schedule.getJob());
        saveLog(List.of(schedule), userId, path, HttpMethodConstants.GET.name(), module, OperationLogType.DELETE.name());
    }

    private Schedule checkScheduleExit(String id) {
        return QueryChain.of(Schedule.class).where(SCHEDULE.ID.eq(id)).oneOpt().orElseThrow(() -> new NSException(Translator.get("schedule_not_exist")));
    }

    private void saveLog(List<Schedule> scheduleList, String userId, String path, String method, String module, String type) {
        // 取出所有的项目id
        if (scheduleList.isEmpty()) {
            return;
        }
        List<String> projectIds = scheduleList.stream().map(Schedule::getProjectId).distinct().toList();
        List<Project> projectList = QueryChain.of(Project.class).where(Project::getId).in(projectIds).list();
        // 生成map key:项目id value:组织id
        Map<String, String> orgMap = projectList.stream().collect(Collectors.toMap(Project::getId, Project::getOrganizationId));
        List<LogDTO> logs = new ArrayList<>();
        scheduleList.forEach(s -> {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(s.getProjectId())
                    .organizationId(orgMap.get(s.getProjectId()))
                    .type(type)
                    .module(module)
                    .method(method)
                    .path(path)
                    .sourceId(s.getResourceId())
                    .content(s.getName())
                    .createUser(userId)
                    .build().getLogDTO();
            logs.add(dto);
        });
        operationLogService.batchAdd(logs);
    }

    public ScheduleParamDTO getScheduleParamDTO(String id) {
        Schedule schedule = checkScheduleExit(id);
        ScheduleParamDTO scheduleParamDTO = new ScheduleParamDTO();
        scheduleParamDTO.setId(schedule.getId());
        if (StringUtils.isNotBlank(schedule.getConfig())) {
            scheduleParamDTO.setConfig(JSON.parseObject(schedule.getConfig(), TaskParameterConfig.class));
        }
        return scheduleParamDTO;
    }

    public ScheduleParamDTO updateScheduleParam(TaskParameterRequest request) {
        Schedule schedule = checkScheduleExit(request.getId());
        Map<String, TaskParameterItem> parameters = request.getParameters() == null
                ? Map.of() : request.getParameters();
        validateTaskParameters(parameters);
        TaskParameterConfig config = new TaskParameterConfig();
        config.setParameters(parameters);
        schedule.setConfig(JSON.toJSONString(config));
        // updateTaskAndSchedule(schedule);
        scheduleService.updateById(schedule);
        JobDataMap jobDataMap = scheduleManager.getDefaultJobDataMap(schedule, schedule.getCronExpression(), schedule.getCreateUser());
        scheduleManager.updateDataMap(new JobKey(schedule.getKey(), schedule.getJob()), jobDataMap);
        ScheduleParamDTO response = new ScheduleParamDTO();
        response.setId(schedule.getId());
        response.setConfig(config);
        return response;
    }

    private void updateTaskAndSchedule(Schedule schedule) {
        scheduleService.updateById(schedule);
        try {
            scheduleService.addOrUpdateCronJob(schedule, new JobKey(schedule.getKey(), schedule.getJob()), new TriggerKey(schedule.getKey(), schedule.getJob()), Class.forName(schedule.getJob()));
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void validateTaskParameters(Map<String, TaskParameterItem> parameters) {
        Set<String> keys = new HashSet<>();
        for (Map.Entry<String, TaskParameterItem> entry : parameters.entrySet()) {
            String mapKey = entry.getKey();
            TaskParameterItem parameter = entry.getValue();
            if (!Set.of("string", "number", "boolean").contains(parameter.getType())
                    || StringUtils.isBlank(mapKey)
                    || StringUtils.isBlank(parameter.getKey())
                    || !mapKey.equals(parameter.getKey())
                    || !keys.add(parameter.getKey())) {
                throw new NSException("任务参数类型不支持或参数 key 重复");
            }
            if ("string".equals(parameter.getType()) && !(parameter.getValue() instanceof String)
                    || "number".equals(parameter.getType()) && !(parameter.getValue() instanceof Number)
                    || "boolean".equals(parameter.getType()) && !(parameter.getValue() instanceof Boolean)) {
                throw new NSException("任务参数 value 与 type 不匹配");
            }
        }
    }

    public void updateCron(ScheduleRequest request, String userId, String path, String module) {
        Schedule schedule = checkScheduleExit(request.id());
        schedule.setCronExpression(request.cron());
        scheduleService.updateById(schedule);
        try {
            scheduleService.addOrUpdateCronJob(schedule, new JobKey(schedule.getKey(), schedule.getJob()),
                    new TriggerKey(schedule.getKey(), schedule.getJob()), Class.forName(schedule.getJob()));
            saveLog(List.of(schedule), userId, path, HttpMethodConstants.GET.name(), module, OperationLogType.UPDATE.name());
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
