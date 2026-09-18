<script setup lang="ts">
import {EnvAuthScopeEnum, type EnvAuthScopeEnumType} from "/@/enums/env-enum.ts";
import useProjectEnvStore, {ALL_PARAM, NEW_ENV_PARAM, NEW_ENV_PARAM_COPY} from "/@/store/modules/useProjectEnvStore.ts";
import type {EnvListItem} from "/@/types/environmental.ts";
import AllParamBox from "/@/views/project-management/environmental/components/AllParamBox.vue";
import EnvParamBox from "/@/views/project-management/environmental/components/EnvParamBox.vue";
import {envManagementAPi} from "/@/api/methods/env-management.ts";
import {useAppStore} from "/@/store";
import {hasAnyPermission} from "/@/utils/permission.ts";
import {isEqual} from "lodash-es";

const showType = ref<EnvAuthScopeEnumType>(EnvAuthScopeEnum.PROJECT); // 展示类型
const store = useProjectEnvStore();
const appStore = useAppStore()
const envList = ref<EnvListItem[]>([]);
const globalEnvRef = ref();
const keyword = ref<string>('');
const envParamBoxRef = ref<InstanceType<typeof EnvParamBox>>();
const excludeActionType = [NEW_ENV_PARAM_COPY, NEW_ENV_PARAM];
const hasUnSaveData = computed(() => envList.value.find((item) => excludeActionType.includes(item.id)));

const activeKey = computed({
  get() {
    return store.currentId;
  },
  set(val) {
    activeKey.value = val;
  },
});
const initData = async (keywordStr = '', initNode = false) => {
  envList.value = await envManagementAPi.listEnv({projectId: appStore.currentProjectId, keyword: keywordStr});
  if (initNode && envList.value.length) {
    store.setCurrentId(envList.value[0].id);
  }
}
const handleCreateEnv = () => {
  const tmpArr = envList.value;
  const unSaveEnv = envList.value.filter((item) => item.id === NEW_ENV_PARAM).length < 1;
  if (unSaveEnv) {
    tmpArr.unshift({
      id: NEW_ENV_PARAM,
      name: '未命名环境',
      description: '',
    });
    store.setCurrentId(NEW_ENV_PARAM);
    envList.value = tmpArr;
  }
}
const openModalTip = (id: string, isNew = false) => {
  const tipContent = isNew ? '当前新增环境未保存，是否保存？' : '有标签页的内容未保存，离开后未保存的内容将丢失，确定要离开吗？';
  const confirmText = isNew ? '保存' : '离开';
  window.$dialog.warning({
    content: tipContent,
    closable: isNew,
    positiveText: confirmText,
    onPositiveClick: async () => {
      if (isNew) {
        try {
          const isNewEnv = envList.value.some((item) => item.id === NEW_ENV_PARAM);
          await envParamBoxRef.value?.saveCallBack(isNewEnv);
        } catch (error) {
          console.log(error);
        }
      } else {
        store.setCurrentId(id);
      }
    }
  })
}
const checkHasNewEnv = () => {
  if (hasUnSaveData.value) {
    openModalTip(hasUnSaveData.value.id, true);
    return true;
  }
  return false;
}
const handleListItemClick = (element: EnvListItem) => {
  if (checkHasNewEnv()) {
    return;
  }
  const {id} = element;
  // 校验是否切换
  if (store.currentId !== id) {
    if (!hasAnyPermission(['PROJECT_ENVIRONMENT:READ+ADD', 'PROJECT_ENVIRONMENT:READ+UPDATE'])) {
      store.setCurrentId(id);
      return;
    }
    const isChangeEnvValue =
        store.currentId === ALL_PARAM
            ? isEqual(store.allParamDetailInfo, store.backupAllParamDetailInfo)
            : isEqual(store.currentEnvDetailInfo, store.backupEnvDetailInfo);
    if (isChangeEnvValue) {
      store.setCurrentId(id);
    } else if (hasUnSaveData.value) {
      openModalTip(id, true);
    } else {
      openModalTip(id);
    }
  }
};
const handleReset = () => {
  const unSaveEnv = envList.value.filter((item) => item.id === NEW_ENV_PARAM).length;
  if (unSaveEnv) {
    envList.value = envList.value.filter((item: any) => item.id !== NEW_ENV_PARAM);
    const excludeMock = envList.value.filter((item) => !item.mock);
    // @desc: 如果没有MOCK环境默认为全局参数
    if (showType.value === 'PROJECT' && !excludeMock.length) {
      store.setCurrentId(ALL_PARAM);
      // @desc: 如果没有MOCK环境默认为MOCK环境
    } else if (excludeMock.length) {
      store.setCurrentId(excludeMock[0].id);
    }
    // @desc: 已经创建恢复最初数据
  } else {
    store.initEnvDetail();
  }
}
const successHandler = (envId: string | undefined) => {
  if (!envId) {
    initData(keyword.value, true);
  } else {
    initData(keyword.value, false);
    store.initEnvDetail();
  }
}
onMounted(() => {
  initData(keyword.value, true);
});
</script>

<template>
  <div class="h-full">
    <n-split default-size="300px" min="300px" class="h-full">
      <template #1>
        <div class="mr-[6px] p-[16px]">
          <template v-if="showType === 'PROJECT'">
            <n-input v-model:value="keyword" placeholder="请输入环境名称" clearable/>
            <div class="p-[8px] text-[var(--color-text-4)]">
              全局请求
            </div>
            <div class="flex items-center justify-between">
              请求头
            </div>
            <n-divider/>
            <div class="env-row p-[8px]">
              <div>环境</div>
              <n-button text class="!mr-0 p-[2px]" :disabled="!!hasUnSaveData" @click="handleCreateEnv">
                <template #icon>
                  <div class="i-mdi:plus-circle-outline"/>
                </template>
              </n-button>
            </div>
            <div>
              <div v-if="envList.length">
                <div v-for="element in envList"
                     :key="element.id"
                     class="env-item cursor-pointer"
                     :class="[activeKey === element.id ? 'env-item-focus' : '']"
                     @click="handleListItemClick(element)">
                  <div class="flex max-w-[100%] grow flex-row items-center justify-between">
                    <div
                        class="one-line-text"
                        :class="{ 'font-medium': element.id === activeKey }">
                      {{ element.name }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </template>
      <template #2>
        <!-- 全局参数 -->
        <all-param-box v-if="showType === 'PROJECT' && activeKey === ALL_PARAM" ref="globalEnvRef"/>
        <!-- 环境变量 -->
        <env-param-box
            v-else-if="showType === 'PROJECT' && activeKey !== ALL_PARAM"
            ref="envParamBoxRef" @reset-env="handleReset"
            @ok="successHandler"
        />
      </template>
    </n-split>
  </div>
</template>

<style scoped>
.env-item-focus {
  background-color: rgb(249, 244, 250);
  .env-item-actions {
    @apply visible;
  }
}
.env-row {
  @apply flex flex-row justify-between;

  &-extra {
    @apply relative;

    opacity: 0;
  }

  &:hover {
    .env-row-extra {
      opacity: 1;
    }
  }
}
</style>