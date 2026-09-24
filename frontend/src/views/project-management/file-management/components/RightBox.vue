<script setup lang="ts">
import type {FileItem, FileListQueryParams} from "/@/types/file.ts";
import type {DataTableColumns} from "naive-ui";
import {useAppStore} from "/@/store";
import {usePagination} from "alova/client";
import {fileManagementApi} from "/@/api/methods/file-management.ts";
import CommonUpload from "/@/components/CommonUpload.vue";

const appStore = useAppStore()
const props = defineProps<{
  activeFolder: string;
  activeFolderType: 'folder' | 'module' | 'storage';
  offspringIds: string[]; // 当前选中文件夹的所有子孙节点id
}>();
const emit = defineEmits<{
  (e: 'init', params: FileListQueryParams): void;
}>();
const keyword = ref('')
const tableFileType = ref('');
const loading = ref(false);
const combine = ref<Record<string, any>>({});
const uploadDrawerVisible = ref(false);
const uploadRef = ref();
const columns: DataTableColumns<FileItem> = [
  {title: '文件别名', key: 'name', width: 270},
  {title: '文件格式', key: 'fileType', width: 100},
  {title: '文件大小', key: 'size', width: 100},
  {title: '标签', key: 'tags'},
  {title: '操作', key: 'operation', fixed: 'right'},
]
const fileUrls = ref<string[]>([]);
const handleAddClick = () => {
  uploadDrawerVisible.value = true;
}

const setLoadListParams = (page: number, pageSize: number) => {
  let moduleIds: string[] = [props.activeFolder, ...props.offspringIds];
  return {
    page, pageSize,
    keyword: keyword.value,
    fileType: tableFileType.value,
    moduleIds,
    projectId: appStore.currentProjectId,
    combine: combine.value,
  }
};
const {data} = usePagination((page, pageSize) => {
  return fileManagementApi.getFileList(setLoadListParams(page, pageSize))
}, {
  watchingStates: [keyword],
  initialData: {total: 0, data: []},
  initialPage: 1, // 初始页码，默认为1
  initialPageSize: 10, // 初始每页数据条数，默认为10
  total: response => response.totalRow,
  data: response => response.records,
  immediate: false
})
const handleCustomUpload = (file: File) => {
  // Implement custom upload logic here
  console.log(file)
  return fileManagementApi.uploadFile({
    request: {
      projectId: appStore.currentProjectId,
      moduleId: ['my', 'all'].includes(props.activeFolder) ? 'root' : props.activeFolder, // 模块id, my/all为根目录
    }, fileList: [file]
  })
}
const startUpload = () => {
  uploadRef.value?.handleManualUpload()
}
</script>

<template>
  <div class="flex h-full flex-col p-[16px]">
    <div class="header">
      <n-button>添加文件</n-button>
      <div class="header-right">
        <n-select/>
        <n-input v-model:value="keyword" placeholder="输入名称搜索"/>
        <!--        <n-radio-group name="fileType">-->
        <!--          <n-radio-button key="Module" value="module" label="模块"/>-->
        <!--          <n-radio-button key="Storage" value="storage" label="存储库" disabled/>-->
        <!--        </n-radio-group>-->
        <!--        <n-radio-group v-model:value="showType" name="showType">-->
        <!--          <n-radio-button key="list" value="list" class="p-[6px]">-->
        <!--            <div class="i-mdi:format-list-bulleted p-[2px]"/>-->
        <!--          </n-radio-button>-->
        <!--          <n-radio-button key="card" value="card" class="p-[6px]">-->
        <!--            <div class="i-mdi:cards-outline p-[4px]"/>-->
        <!--          </n-radio-button>-->
        <!--        </n-radio-group>-->
      </div>
    </div>
    <n-spin class="h-[calc(100%-48px)] w-full" :show="loading">
      <n-data-table :columns="columns" :data="data">
        <template #empty>
          <div class="flex w-full items-center justify-center p-[16px]">
            暂无数据，请
            <n-button text class="ml-[8px]" @click="handleAddClick">
              添加文件
            </n-button>
          </div>
        </template>
      </n-data-table>
    </n-spin>
  </div>
  <n-drawer v-model:show="uploadDrawerVisible" :width="680">
    <n-drawer-content>
      <template #header>
        添加文件
      </template>
      <div class="mb-[8px] flex items-center justify-between text-[var(--color-text-1)]">
        文件类型
        <div class="flex items-center text-[12px] leading-[16px] text-red">
          <div class="mr-[2px] i-mdi:exclamation-thick"/>
          切换文件类型，已选/已上传文件列表会清空
        </div>
      </div>
      <div class="mb-[24px] grid grid-cols-2 gap-[16px]">

      </div>
      <common-upload ref="uploadRef" v-model:value="fileUrls" :custom-upload="handleCustomUpload"/>
      <template #footer>
        <n-flex>
          <n-button secondary>取消</n-button>
          <n-button secondary>后台上传</n-button>
          <n-button type="primary" @click="startUpload">开始上传</n-button>
        </n-flex>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>
.header {
  @apply flex items-center justify-between;

  margin-bottom: 16px;

  .header-right {
    @apply ml-auto flex items-center justify-end;

    width: 70%;
    gap: 8px;

    .show-type-icon {
      :deep(.arco-radio-button-content) {
        @apply flex;

        padding: 4px;
        line-height: 20px;
      }
    }
  }
}
</style>