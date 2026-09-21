<script setup lang="ts">
import {useForm} from "alova/client";
import {projectTaskApi} from "/@/api/methods/task-center/project.ts";
import {useAppStore} from "/@/store";
import {getGenerateId} from "/@/utils";
import CronSelect from "/@/components/CronSelect.vue";

const appStore = useAppStore()
const showModal = defineModel<boolean>('showModal')
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
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog">
    <template #header>
      <div>标题</div>
    </template>
    <div>
      <n-form :model="form">
        <n-form-item label="任务名称">
          <n-input v-model:value="form.name"/>
        </n-form-item>
        <n-form-item label="任务描述">
          <n-input v-model:value="form.job"/>
        </n-form-item>
        <n-form-item label="执行规则">
          <cron-select v-model:model-value="form.cronExpression"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <div>
        <n-button secondary @click="handleCancel">取消</n-button>
        <n-button type="primary" @click="handleSave">保存</n-button>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>