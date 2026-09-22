<script setup lang="ts">
import {useForm} from "alova/client";
import {projectTaskApi} from "/@/api/methods/task-center/project.ts";
import {useAppStore} from "/@/store";
import {getGenerateId} from "/@/utils";
import CronSelect from "/@/components/CronSelect.vue";
import type {SelectOption} from "naive-ui";
import type {editType} from "/@/types/task-center.ts";

const appStore = useAppStore()
const showModal = defineModel<boolean>('showModal')
const props = defineProps<{ currentSchedule?: editType; }>();
const isEdit = computed(() => !!props.currentSchedule?.id);
const {form, send, reset} = useForm((formData) => projectTaskApi.saveSchedule(formData), {
  initialForm: {
    name: '',
    resourceId: '',
    enable: true,
    cronExpression: '',
    key: getGenerateId(),
    projectId: appStore.currentProjectId,
    job: '',
    type: 'CRON',
    resourceType: '',
  },
  immediate: false,
  resetAfterSubmiting: true
})
const handleSave = () => {
  send().then(() => {
    showModal.value = false
    reset()
  })
}
const handleCancel = () => {
  showModal.value = false
  reset()
}
const resourceTypeOptions = computed<Array<SelectOption>>(() => {
  return [
    {label: '安全监控', value: 'aqjk'},
    {label: '矿压', value: 'ky'},
    {label: '水害防治', value: 'shfz'},
    {label: '尾矿库', value: 'wkk'},
    {label: 'GNSS', value: 'GNSS'},
    {label: '其他', value: 'other'},
  ];
});
watchEffect(() => {
  if (props.currentSchedule?.id) {
    if (props.currentSchedule) {
      form.value.id = props.currentSchedule.id;
      form.value.name = props.currentSchedule.name;
      form.value.job = props.currentSchedule.job;
      form.value.cronExpression = props.currentSchedule.cronExpression;
      form.value.resourceType = props.currentSchedule.resourceType;
      form.value.enable = props.currentSchedule.enable;
    }
  } else {
    reset()
  }
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog">
    <template #header>
      <div v-if="isEdit">
        <span>{{ `更新任务${props.currentSchedule?.name}` }}</span>
      </div>
      <span v-else>创建任务</span>
    </template>
    <div>
      <n-form :model="form">
        <n-form-item label="任务名称">
          <n-input v-model:value="form.name"/>
        </n-form-item>
        <n-form-item v-if="!isEdit" label="job">
          <n-input v-model:value="form.job"/>
        </n-form-item>
        <n-form-item label="执行规则">
          <cron-select v-model:model-value="form.cronExpression"/>
        </n-form-item>
        <n-form-item>
          <n-select v-model:value="form.resourceType" :options="resourceTypeOptions"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <n-flex>
        <n-button secondary @click="handleCancel">取消</n-button>
        <n-button type="primary" @click="handleSave">保存</n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>