<script setup lang="ts">
import {usePagination} from "alova/client";
import {projectApi} from "/@/api/methods/project.ts";
import AddProjectModal from "/@/views/setting/project/AddProjectModal.vue";
import type {editType} from "/@/types/project.ts";
import {type DataTableColumns, NButton, NFlex, NSwitch} from "naive-ui";

const keyword = ref('')
const projectVisible = ref(false)
const currentUpdateProject = ref<editType>();
const {data, send: tableSearch} = usePagination((page, pageSize) => projectApi.getProjectPage({
  page,
  pageSize,
  keyword: keyword.value
}), {
  watchingStates: [keyword],
  initialData: {total: 0, data: []},
  initialPage: 1, // 初始页码，默认为1
  initialPageSize: 10, // 初始每页数据条数，默认为10
  total: response => response.totalRow,
  data: response => response.records,
  immediate: false
})
const handleAdd = () => {
  projectVisible.value = true;
}
const handleEdit = (record: editType) => {
  currentUpdateProject.value = record;
  projectVisible.value = true;
}
const columns: DataTableColumns<editType> = [
  {title: '项目编号', key: 'num'},
  {title: '项目名称', key: 'name'},
  {title: '成员', key: 'memberCount'},
  {
    title: '状态', key: 'enable', render(record: editType) {
      return h(NSwitch, {value: record.enable, size: 'small'})
    }
  },
  {title: '描述', key: 'description'},
  {
    title: '操作', key: 'operation', fixed: 'right', width: 200, render: (record: editType) => {
      if (record.enable) {
        return h(NFlex, {}, {
          default: () => [
            h(NButton, {type: 'primary', text: true, onClick: () => handleEdit(record)}, {default: () => '编辑'}),
            h(NButton, {type: 'info', text: true}, {default: () => '配置'}),
            h(NButton, {type: 'error', text: true}, {default: () => '删除'})
          ]
        });
      } else {
        return h(NButton, {type: 'error', text: true}, {default: () => '删除'})
      }
    }
  },
]
const handleAddProjectCancel = (shouldSearch: boolean) => {
  projectVisible.value = false;
  if (shouldSearch) {
    tableSearch();
  }
}
onMounted(() => {
  tableSearch()
})
</script>

<template>
  <n-card>
    <template #header>
      <n-button type="primary" size="small" @click="handleAdd">add</n-button>
    </template>
    <template #header-extra>
      <n-input v-model:value="keyword"/>
    </template>
    <n-data-table :data="data" :columns="columns"/>
  </n-card>
  <add-project-modal v-model:model-value="projectVisible"
                     :current-project="currentUpdateProject"
                     @cancel="handleAddProjectCancel"/>
</template>

<style scoped>

</style>