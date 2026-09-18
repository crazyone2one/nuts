<script setup lang="ts">
import {EnvTabTypeEnum} from "/@/enums/env-enum.ts";
import useProjectEnvStore from "/@/store/modules/useProjectEnvStore.ts";
import HttpTab from "/@/views/project-management/environmental/components/env-params/HttpTab.vue";
import DataBaseTab from "/@/views/project-management/environmental/components/env-params/DataBaseTab.vue";
import HostTab from "/@/views/project-management/environmental/components/env-params/HostTab.vue";
import type {FormInst} from "naive-ui";
import {cloneDeep} from "lodash-es";
import FtpTab from "/@/views/project-management/environmental/components/env-params/FtpTab.vue";
import {useLeaveUnSaveTip} from "/@/hooks/useLeaveUnSaveTip.ts";
import {envManagementAPi} from "/@/api/methods/env-management.ts";

const activeKey = ref<string>(EnvTabTypeEnum.ENVIRONMENT_DATABASE);
const store = useProjectEnvStore();
const formRef = ref<FormInst | null>(null)
const envKeyword = ref('');
const hostTabRef = ref();
const form = reactive({
  name: '',
  description: '',
});
const {setIsSave} = useLeaveUnSaveTip({
  leaveTitle: '温馨提示',
  leaveContent: '有标签页的内容未保存，离开后未保存的内容将丢失，确定要离开吗？',
  tipType: 'warning',
});
const emit = defineEmits<{
  (e: 'ok', envId: string | undefined): void;
  (e: 'resetEnv'): void;
}>();
const getParameters = (isNew = false) => {
  const paramsConfig = cloneDeep(store.currentEnvDetailInfo.config);
  if (isNew) {
    store.currentEnvDetailInfo.name = store.currentEnvDetailInfo.name
        ? store.currentEnvDetailInfo.name
        : '未命名环境';
  }
  return {
    ...cloneDeep(store.currentEnvDetailInfo),
    config: {
      ...paramsConfig
    },
  };
}
// const {} = useUploader(({ file, name }) => envManagementAPi.updateOrAddEnv({fileList: [], request: getParameters(isNew)}))
const saveCallBack = async (isNew = false) => {
  store.currentEnvDetailInfo.mock = true;
  await envManagementAPi.updateOrAddEnv({fileList: [], request: getParameters(isNew)})
  setIsSave(true);
  window.$message.success(store.currentEnvDetailInfo.id ? '更新成功' : '保存成功')
  emit('ok', store.currentEnvDetailInfo.id);
}
const handleSave = () => {
  formRef.value?.validate(async (err) => {
    if (!err) {
      if (activeKey.value === EnvTabTypeEnum.ENVIRONMENT_HOST) {
        hostTabRef.value?.validateForm(saveCallBack);
      } else {
        envKeyword.value = '';
        await nextTick();
        await saveCallBack();
      }
    }
  })
}
const handleReset = () => {
  formRef.value?.restoreValidation();
  form.name = ''
  form.description = ''
  emit('resetEnv');
};
watchEffect(() => {
  if (store.currentId) {
    store.initEnvDetail();
  }
});
watchEffect(() => {
  if (store.currentEnvDetailInfo) {
    const {currentEnvDetailInfo} = store;
    form.name = currentEnvDetailInfo.name;
    form.description = currentEnvDetailInfo.description as string;
  }
});
defineExpose({
  saveCallBack,
});
</script>

<template>
  <div class="page">
    <div class="header">
      <n-form ref="formRef" :model="form">
        <n-form-item class="mb-[16px]" path="name" label="环境名称"
                     :rule="{required:true,message:'环境名称不能为空'}">
          <n-input v-model:value="form.name" placeholder="请输入环境名称" :maxlength="255"
                   @blur="store.currentEnvDetailInfo.name = form.name"/>
        </n-form-item>
        <n-form-item class="mb-[16px]" path="description" label="描述">
          <n-input v-model:value="form.description" type="textarea" :maxlength="1000" :autosize="{minRows:1}"
                   placeholder="请输入描述" clearable
                   @blur="store.currentEnvDetailInfo.description = form.description"/>
        </n-form-item>
      </n-form>
      <div class="content flex w-full flex-col">
        <n-tabs type="line" animated v-model:value="activeKey">
          <n-tab-pane :name="EnvTabTypeEnum.ENVIRONMENT_DATABASE" tab="Database">
            <data-base-tab/>
          </n-tab-pane>
          <n-tab-pane :name="EnvTabTypeEnum.ENVIRONMENT_FTP" tab="FTP">
            <ftp-tab/>
          </n-tab-pane>
          <n-tab-pane :name="EnvTabTypeEnum.ENVIRONMENT_HOST" tab="HOST" disabled>
            <host-tab ref="hostTabRef"/>
          </n-tab-pane>
          <n-tab-pane :name="EnvTabTypeEnum.ENVIRONMENT_PARAM" tab="环境变量" disabled>
            not yet..
          </n-tab-pane>
          <n-tab-pane :name="EnvTabTypeEnum.ENVIRONMENT_HTTP" tab="HTTP" disabled>
            <http-tab/>
          </n-tab-pane>

          <template #suffix>
            <n-flex v-permission="['PROJECT_ENVIRONMENT:READ+UPDATE']" :style="{ width: '100%' }">
              <n-button secondary @click="handleReset">取消</n-button>
              <n-button type="primary" @click="handleSave">保存</n-button>
            </n-flex>
          </template>
        </n-tabs>
      </div>

    </div>
  </div>
</template>

<style scoped>
.page {
  position: relative;
  transform: scale3d(1, 1, 1);
  height: 100%;

  .header {
    padding: 16px 16px 0;
  }

  .content {
    overflow-y: auto;
    padding: 0 16px;
    max-height: calc(100% - 320px);
  }
}
</style>