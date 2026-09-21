<script setup lang="ts">

import {useForm, useRequest} from "alova/client";
import {projectTaskApi} from "/@/api/methods/task-center/project.ts";

const active = defineModel<boolean>('active')
const props = defineProps<{ id: string }>()
const {form} = useForm((formData) => projectTaskApi.updateProjectScheduleParam(formData), {
  initialForm: {
    sensorCode: ''
  }
})
const {send: fetchTaskParam} = useRequest(id => projectTaskApi.projectGetScheduleParam(id), {immediate: false})
const getTaskParam = (id: string) => {
  fetchTaskParam(id).then(response => {
    console.log(response)
  })
}
const handleCancel = () => {
  active.value = false
}
const handleSave = () => {
  // Implementation for saving the form
  console.log(form.value)
}
watch(() => props.id, (newValue) => {
  if (newValue) {
    getTaskParam(newValue)
  }
})
</script>

<template>
  <n-drawer v-model:show="active" :width="502" :close-on-esc="false" :mask-closable="false">
    <n-drawer-content>
      <template #header>
        编辑参数
      </template>
      <div>
        <n-form :model="form">
          <n-form-item label="传感器编码">
            <n-input v-model:value="form.sensorCode"/>
          </n-form-item>
        </n-form>
      </div>
      <template #footer>
        <n-button secondary @click="handleCancel">取消</n-button>
        <n-button type="primary" @click="handleSave">保存</n-button>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>