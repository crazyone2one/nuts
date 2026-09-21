<script setup lang="ts">

import {useRequest} from "alova/client";
import {projectTaskApi} from "/@/api/methods/task-center/project.ts";
import type {TaskParameterItem} from "/@/types/task-center.ts";
import ShowOrEdit from "/@/components/ShowOrEdit.vue";
import {type DataTableColumns, NButton, NInputNumber, NSelect, NSwitch} from "naive-ui";

const active = defineModel<boolean>('active')
const props = defineProps<{ id: string }>()
const parameters = ref<TaskParameterItem[]>([]);
const editingIndex = ref<number | null>(null);
const {send: fetchTaskParam} = useRequest(id => projectTaskApi.projectGetScheduleParam(id), {immediate: false})
const getTaskParam = (id: string) => {
  fetchTaskParam(id).then(response => {
    parameters.value = Object.values(response.config?.parameters ?? {});
  })
}
const addParameter = () => {
  parameters.value.push({
    label: '', key: '', type: 'string', value: '', enabled: true
  });
  editingIndex.value = parameters.value.length - 1;
};
const removeParameter = (index: number) => {
  parameters.value.splice(index, 1);
  if (editingIndex.value === index) {
    editingIndex.value = null;
  } else if (editingIndex.value !== null && editingIndex.value > index) {
    editingIndex.value -= 1;
  }
};
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
const columns: DataTableColumns<TaskParameterItem> = [
  {
    title: '参数名称', key: 'label', render(record, index) {
      return h(ShowOrEdit, {
        value: record.label, isEdit: editingIndex.value === index,
        onUpdateValue: (value) => {
          record.label = value;
        }, size: 'small'
      });
    }
  },
  {
    title: '参数 key', key: 'key', render(record, index) {
      return h(ShowOrEdit, {
        value: record.key, isEdit: editingIndex.value === index,
        onUpdateValue: (value) => {
          record.key = value;
        }, size: 'small'
      });
    }
  },
  {
    title: '参数类型', key: 'type', render(record) {
      return h(NSelect, {
        value: record.type,
        options: [
          {label: 'String', value: 'string'},
          {label: 'Number', value: 'number'},
          {label: 'Boolean', value: 'boolean'}
        ],
        onUpdateValue: (value) => {
          record.type = value;
          normalizeValue(record);
        },
        size: 'small'
      });
    }
  },
  {
    title: '参数值', key: 'value', render(record, index) {
      if (record.type === 'string') {
        return h(ShowOrEdit, {
          value: String(record.value),
          isEdit: editingIndex.value === index,
          onUpdateValue: (value) => {
            record.value = value;
          },
          size: 'small'
        });
      }
      if (record.type === 'number') {
        return h(NInputNumber, {
          value: typeof record.value === 'number' ? record.value : null,
          placeholder: '值',
          onUpdateValue: (value) => {
            record.value = value ?? 0;
          },
          size: 'small'
        });
      }
      if (record.type === 'boolean') {
        return h(NSwitch, {
          value: record.value === true,
          onUpdateValue: (value) => {
            record.value = value;
          },
          size: 'small'
        });
      }
    }
  },
  {
    title: '是否生效', key: 'enabled', render(record) {
      return h(NSwitch, {
        value: record.enabled, onUpdateValue: (value) => {
          record.enabled = value;
        },
        size: 'small'
      })
    }
  },
  {
    title: '操作', key: 'operation', render(_, index) {
      return h(NButton, {onClick: () => removeParameter(index), size: 'small'}, {default: () => '删除'})
    }
  }
]
watch(() => props.id, (newValue) => {
  if (newValue) {
    getTaskParam(newValue)
  }
}, {deep: true})
</script>

<template>
  <n-drawer v-model:show="active" :width="900" :close-on-esc="false" :mask-closable="false">
    <n-drawer-content>
      <template #header>
        编辑参数
      </template>
      <div>
        <n-button secondary class="mb-[16px]" @click="addParameter">添加参数</n-button>
        <n-data-table :columns="columns" :data="parameters"/>
      </div>
      <template #footer>
        <n-flex>
          <n-button secondary @click="handleCancel">取消</n-button>
          <n-button type="primary" @click="handleSave">保存</n-button>
        </n-flex>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>