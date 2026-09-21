<script setup lang="ts">
import {type DataTableColumns, NButton, NFlex, NSwitch} from "naive-ui";
import type {TaskCenterSystemTaskItem} from "/@/types/task-center.ts";
import {usePagination, useRequest} from "alova/client";
import {projectTaskApi} from "/@/api/methods/task-center/project.ts";
import {systemTaskApi} from "/@/api/methods/task-center/system.ts";
import AddScheduleModal from "/@/views/task-center/components/AddScheduleModal.vue";
import {hasAnyPermission} from "/@/utils/permission.ts";
import CronSelect from "/@/components/CronSelect.vue";
import EditParam from "/@/views/task-center/components/EditParam.vue";

const props = defineProps<{
  type: 'system' | 'org' | 'project';
  mode?: 'modal' | 'normal';
}>();
const keyword = ref('')
const taskId = ref('')
const showAdd = ref(false)
const showParamDrawer = ref(false)
const getCurrentPermission = (action: 'DELETE' | 'EDIT') => {
  return {
    system: {
      DELETE: 'SYSTEM_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'SYSTEM_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
    org: {
      DELETE: 'ORGANIZATION_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'ORGANIZATION_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
    project: {
      DELETE: 'PROJECT_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'PROJECT_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
  }[props.type][action];
}
const columns: DataTableColumns<TaskCenterSystemTaskItem> = [
  {title: '任务 ID', key: 'num'},
  {title: '任务名称', key: 'name'},
  {
    title: '状态', key: 'enable', render(record) {
      return h(NSwitch, {
        value: record.enable, size: 'small',
        disabled: !hasAnyPermission([getCurrentPermission('EDIT')]),
        onUpdateValue: () => handleEnableChange(record)
      });
    }
  },
  {title: '类型', key: 'resourceType'},
  {
    title: '运行规则', key: 'cronExpression', render(record) {
      if (hasAnyPermission([getCurrentPermission('EDIT')])) {
        return h(CronSelect, {
          modelValue: record.cronExpression, size: 'small',
          onChange: (v) => handleRunRuleChange(v, record)
        })
      }
      return record.cronExpression;
    }
  },
  {title: '上次完成时间', key: 'lastTime'},
  {title: '下次执行时间', key: 'nextTime'},
  {
    title: '操作', key: 'operation', fixed: 'right', width: '200', render(record) {
      return h(NFlex, {}, {
        default: () => {
          return [
            h(NButton, {text: true, onClick: () => deleteTask(record)}, {default: () => '删除'}),
            h(NButton, {text: true, onClick: () => handleParam(record)}, {default: () => '参数'})
          ]
        }
      })
    }
  },
]
const {send: fetchEnableChange} = useRequest(id => projectTaskApi.projectScheduleSwitch(id), {immediate: false})
const handleEnableChange = (record: TaskCenterSystemTaskItem) => {
  fetchEnableChange(record.id).then(() => {
    window.$message.success(record.enable ? '任务关闭成功' : '任务开启成功');
    loadList()
  })
}
const handleRunRuleChange = async (value: string, record: TaskCenterSystemTaskItem) => {
  try {
    record.runRuleLoading = true;
    await projectTaskApi.projectEditCron(value, record.id);
    window.$message.success('更新成功')
  } catch (error) {
    console.log(error);
  } finally {
    record.runRuleLoading = false;
  }
};
const handleParam = (record: TaskCenterSystemTaskItem) => {
  taskId.value = record.id;
  showParamDrawer.value = true;
}
const {send: fetchDelete} = useRequest((id) => projectTaskApi.projectDeleteSchedule(id), {immediate: false})
const deleteTask = (record: TaskCenterSystemTaskItem) => {
  console.log(record);
  window.$dialog.error({
    title: `确认删除 ${record.name} 吗？`,
    content: '删除后，定时任务停止，请谨慎操作！',
    positiveText: '确认删除',
    negativeText: '取消',
    maskClosable: false,
    onPositiveClick: () => {
      fetchDelete(record?.id || '').then(() => {
        window.$message.success('');
        loadList();
      })
    }
  })
}
const {data, send: loadList} = usePagination((page, pageSize) => {
  const query = {page, pageSize, keyword: keyword.value}
  return props.type === 'project' ? projectTaskApi.getProjectScheduleList(query) : systemTaskApi.getSystemScheduleList(query)
}, {
  watchingStates: [keyword],
  initialData: {total: 0, data: []},
  initialPage: 1, // 初始页码，默认为1
  initialPageSize: 10, // 初始每页数据条数，默认为10
  total: response => response.totalRow,
  data: response => response.records,
  immediate: false
})
onMounted(() => {
  loadList()
})
</script>

<template>
  <div class="my-[16px] flex items-center justify-end">
    <n-button @click="showAdd = true">add</n-button>
    <n-input v-model:value="keyword" placeholder="通过 ID/名称搜索"/>
  </div>
  <n-data-table :columns="columns" :data="data"/>
  <add-schedule-modal v-model:show-modal="showAdd"/>
  <edit-param v-model:active="showParamDrawer" :id="taskId"/>
</template>

<style scoped>

</style>