<script setup lang="ts">

import {useRequest} from "alova/client";
import {projectTaskApi} from "/@/api/methods/task-center/project.ts";
import type {TaskParameterItem} from "/@/types/task-center.ts";

const active = defineModel<boolean>('active')
const props = defineProps<{ id: string }>()
const parameters = ref<TaskParameterItem[]>([]);
const {send: fetchTaskParam} = useRequest(id => projectTaskApi.projectGetScheduleParam(id), {immediate: false})
const getTaskParam = (id: string) => {
  fetchTaskParam(id).then(response => {
    parameters.value = Object.values(response.config?.parameters ?? {});
  })
}
const addParameter = () => parameters.value.push({
  label: '', key: '', type: 'string', value: '', enabled: true
});
const removeParameter = (index: number) => parameters.value.splice(index, 1);
const normalizeValue = (item: TaskParameterItem) => {
  if (item.type === 'string') item.value = String(item.value ?? '');
  if (item.type === 'number') item.value = Number(item.value);
  if (item.type === 'boolean') item.value = Boolean(item.value);
};
const handleCancel = () => {
  active.value = false
}
const handleSave = async () => {
  parameters.value.forEach(normalizeValue);
  const parameterMap = Object.fromEntries(parameters.value.map(parameter => [parameter.key, parameter]));
  await projectTaskApi.updateProjectScheduleParam({
    id: props.id, parameters: parameterMap
  }).send(true);
  window.$message.success('保存成功');
  active.value = false;
}
watch(() => props.id, (newValue) => {
  if (newValue) {
    getTaskParam(newValue)
  }
})
</script>

<template>
  <n-drawer v-model:show="active" :width="900" :close-on-esc="false" :mask-closable="false">
    <n-drawer-content>
      <template #header>
        编辑参数
      </template>
      <div>
        <n-button secondary class="mb-[16px]" @click="addParameter">添加参数</n-button>
        <n-flex v-for="(item, index) in parameters" :key="`${item.key}-${index}`">
          <div>
            <n-input v-model:value="item.label" class="w-[90px]" placeholder="名称" size="small"/>
          </div>
          <div>
            <n-input v-model:value="item.key" class="w-[90px]" placeholder="key" size="small"/>
          </div>
          <n-select v-model:value="item.type" class="w-[110px]" size="small"
                    :options="[
                      {label: 'String', value: 'string'},
                      {label: 'Number', value: 'number'},
                      {label: 'Boolean', value: 'boolean'}
                    ]" @update:value="normalizeValue(item)"/>
          <div>
            <n-input v-if="item.type === 'string'" :value="String(item.value)" size="small"
                     class="grow" placeholder="值" @update:value="item.value = $event"/>
            <n-input-number v-else-if="item.type === 'number'" size="small"
                            :value="typeof item.value === 'number' ? item.value : null"
                            class="grow" placeholder="值"
                            @update:value="item.value = $event ?? 0"/>
            <n-switch v-else size="small" :value="item.value === true"
                      @update:value="item.value = $event"/>
          </div>
          <n-switch v-model:value="item.enabled" size="small"/>
          <div>
            <n-button quaternary type="error" @click="removeParameter(index)">删除</n-button>
          </div>
        </n-flex>
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