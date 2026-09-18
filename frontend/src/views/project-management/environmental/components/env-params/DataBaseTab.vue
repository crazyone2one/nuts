<script setup lang="ts">
import useProjectEnvStore from "/@/store/modules/useProjectEnvStore.ts";
import {type DataTableColumns, NButton, NDivider} from "naive-ui";
import type {DataSourceItem} from "/@/types/environmental.ts";
import AddDatabaseModal from "/@/views/project-management/environmental/components/env-params/AddDatabaseModal.vue";

const store = useProjectEnvStore();
const addVisible = ref(false);
const currentId = ref('');
const isCopy = ref<boolean>(false);

const innerParam = computed(() => store.currentEnvDetailInfo.config.dataSources || []);
const handleAdd = () => {
  currentId.value = '';
  isCopy.value = false;
  addVisible.value = true;
}
const handleEdit = (record: any) => {
  isCopy.value = false;
  currentId.value = record.id;
  addVisible.value = true;
};
const handleCopy = (record: any) => {
  isCopy.value = true;
  currentId.value = record.id;
  addVisible.value = true;
};
const columns: DataTableColumns<DataSourceItem> = [
  {title: '数据源名称', key: 'dataSource'},
  {title: '驱动', key: 'driver'},
  {title: 'URL', key: 'dbUrl'},
  {title: '用户名', key: 'username'},
  {title: '最大连接数', key: 'poolMax'},
  {title: '超时时间 (ms）', key: 'timeout'},
  {
    title: '操作', key: 'operation', fixed: 'right', width: 170, render(row) {
      return h('div', {class:'flex flex-row flex-nowrap items-center'}, {
        default: () => [
          h(NButton, {text: true, class: '!mr-0', onClick: () => handleCopy(row)}, {default: () => '复制'}),
          h(NDivider, {vertical: true}),
          h(NButton, {text: true, class: '!mr-0', onClick: () => handleEdit(row)}, {default: () => '编辑'}),
          h(NDivider, {vertical: true}),
          h(NButton, {text: true, class: '!mr-0'}, {default: () => '删除'}),
        ]
      })
    }
  },
]
</script>

<template>
  <div>
    <div class="flex items-center justify-between">
      <n-button v-permission="['PROJECT_ENVIRONMENT:READ+UPDATE']" @click="handleAdd">添加数据源</n-button>
      <div>
        <n-input placeholder="通过名称搜索" class="w-[240px]"/>
      </div>
    </div>
    <n-data-table class="mt-[16px]" :data="innerParam" :columns="columns"/>
  </div>
  <add-database-modal v-model:model-value="addVisible" :current-id="currentId" :is-copy="isCopy"/>
</template>

<style scoped>

</style>