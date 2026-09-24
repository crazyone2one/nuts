<script setup lang="ts">
import RightBox from "/@/views/project-management/file-management/components/RightBox.vue";
import FolderTree from "/@/views/project-management/file-management/components/FolderTree.vue";
import {useRequest} from "alova/client";
import {fileManagementApi} from "/@/api/methods/file-management.ts";
import type {FileListQueryParams} from "/@/types/file.ts";

type FileShowType = 'Module' | 'Storage';
const modulesCount = ref<Record<string, number>>({});
const myFileCount = ref(0);
const allFileCount = ref(0);
const activeFolder = ref<string>('all');
const activeFolderType = ref<'folder' | 'module' | 'storage'>('folder');
const isExpandAll = ref(false);
const tableFilterParams = ref<FileListQueryParams>({
  moduleIds: [],
  fileType: '',
  projectId: '',
});
const showType = ref<FileShowType>('Module');
const offspringIds = ref<string[]>([]);
const handleModuleTableInit = (params: FileListQueryParams) => {
  initModulesCount({
    ...params,
    combine: {
      ...params.combine,
      storage: showType.value === 'Module' ? 'minio' : 'git', // 这里因为存在切换我的、全部文件夹时，激活的是存储库列表，所以还要区分一下当前展示的类型是啥
    },
  });
  tableFilterParams.value = {...params};
}
const setActiveFolder = (id: string) => {
  activeFolder.value = id;
  if (['my', 'all'].includes(id)) {
    activeFolderType.value = 'folder';
  } else {
    activeFolderType.value = 'storage';
  }
}
const getFolderClass = (id: string) => {
  return activeFolder.value === id ? 'folder-text folder-text--active' : 'folder-text';
}
const handleSelect = (key: string) => {
  if (key === 'module') {
    document.querySelector('#allPlus')?.dispatchEvent(new Event('click'));
  } else {
    // storageDrawerVisible.value = true;
  }
}
const {send: fetchModulesCount} = useRequest(params => fileManagementApi.getModulesCount(params), {immediate: false})
const initModulesCount = async (params: FileListQueryParams) => {
  fetchModulesCount(params).then(res => {
    modulesCount.value = res;
    myFileCount.value = modulesCount.value.my || 0;
    allFileCount.value = modulesCount.value.all || 0;
  })
}
const changeShowType = (type: string) => {
  showType.value = type as FileShowType;
  if (type === 'Storage') {
    initModulesCount({
      ...tableFilterParams.value,
      combine: {
        ...tableFilterParams.value.combine,
        storage: 'git',
      },
    });
  } else {
    initModulesCount({
      ...tableFilterParams.value,
      combine: {
        ...tableFilterParams.value.combine,
        storage: 'minio',
      },
    });
  }
}
</script>

<template>
  <n-card class="page">
    <n-split direction="horizontal" default-size="300px" min="300px">
      <template #1>
        <div class="p-[16px]">
          <div class="folder" @click="setActiveFolder('my')">
            <div :class="getFolderClass('my')">
              <div class="folder-icon i-mdi:folder"/>
              <div class="folder-name">我的文件</div>
              <div class="folder-count">({{ myFileCount }})</div>
            </div>
          </div>
          <div class="folder mb-[8px]" @click="setActiveFolder('all')">
            <div :class="getFolderClass('all')">
              <div class="folder-icon i-mdi:folder"/>
              <div class="folder-name">全部文件</div>
              <div class="folder-count">({{ allFileCount }})</div>
            </div>
            <div class="ml-auto flex items-center">
              <n-tooltip trigger="hover">
                <template #trigger>
                  <n-button text class="!mr-0 p-[4px]" @click="isExpandAll = !isExpandAll">
                    <template #icon>
                      <div :class="isExpandAll?'i-mdi:folder-open':'i-mdi:folder'"/>
                    </template>
                  </n-button>
                </template>
                {{ isExpandAll ? '收起全部子模块' : '展开全部子模块' }}
              </n-tooltip>
              <n-dropdown trigger="click" :options="[]" @select="handleSelect">
                <n-button text class="!mr-0 p-[2px]">
                  <template #icon>
                    <div class="i-mdi:add-circle"/>
                  </template>
                </n-button>
              </n-dropdown>
            </div>
          </div>
          <n-radio-group v-model:value="showType" name="showType" size="small" @update:value="changeShowType">
            <n-radio-button key="Module" value="Module" label="模块"/>
            <n-radio-button key="Storage" value="Storage" label="存储库" disabled/>
          </n-radio-group>
          <div v-show="showType === 'Module'">
            <folder-tree/>
          </div>
        </div>
      </template>
      <template #2>
        <right-box :active-folder="activeFolder"
                   :active-folder-type="activeFolderType"
                   :offspring-ids="offspringIds"
                   @init="handleModuleTableInit"/>
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>
.page {
  @apply h-full;

  min-width: 1000px;
  border-radius: 8px;
  .folder {
    @apply flex cursor-pointer items-center justify-between;

    padding: 8px 4px;
    border-radius: 2px;
    &:hover {
      background-color: rgb(249, 244, 250);
    }
    .folder-text {
      @apply flex cursor-pointer items-center;
      .folder-icon {
        margin-right: 4px;
        color: #959598;
      }
      .folder-name {
        color: #323233;
      }
      .folder-count {
        margin-left: 4px;
        color: #959598;
      }
    }
    .folder-text--active {
      .folder-icon,
      .folder-name,
      .folder-count {
        color: rgb(120, 56, 135);
      }
    }
  }
  .file-show-type {
    @apply grid grid-cols-2;

    margin-bottom: 8px;
    :deep(.arco-radio-button-content) {
      @apply text-center;
    }
  }
}
</style>