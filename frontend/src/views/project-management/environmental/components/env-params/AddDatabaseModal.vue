<script setup lang="ts">
import useProjectEnvStore from "/@/store/modules/useProjectEnvStore.ts";
import type {DataSourceItem} from "/@/types/environmental.ts";
import type {FormInst} from "naive-ui";
import {getGenerateId} from "/@/utils";

const showModal = defineModel<boolean>({default: false})
const props = defineProps<{ currentId: string; isCopy: boolean; }>();
const store = useProjectEnvStore();
const initForm = {
  id: '',
  dataSource: '',
  driverId: '',
  dbUrl: '',
  username: '',
  password: '',
  poolMax: 1,
  timeout: 1000,
};
const formRef = ref<FormInst | null>(null)
const form = ref<DataSourceItem>({
  ...initForm,
});
const driverOption = ref<{ label: string; value: string }[]>([
  {label: 'system&com.mysql.cj.jdbc.Driver', value: 'com.mysql.cj.jdbc.Driver'}
]);
const formReset = () => {
  form.value = {
    id: '',
    dataSource: '',
    driverId: '',
    dbUrl: '',
    username: '',
    password: '',
    poolMax: 1,
    timeout: 1000,
  };
};
const handleSubmit = () => {
  formRef.value?.validate(err => {
    if (!err) {
      const isExist = store.currentEnvDetailInfo.config.dataSources.some(
          (item) => item.dataSource === form.value.dataSource && item.id !== form.value.id
      );
      if (isExist) {
        window.$message.error("数据源名称已存在")
        return;
      }
      const {driverId} = form.value;
      try {
        const index = store.currentEnvDetailInfo.config.dataSources.findIndex((item: any) => item.id === form.value.id);
        if (index > -1 && !props.isCopy) {
          store.currentEnvDetailInfo.config.dataSources.splice(index, 1, form.value);
        } else if (index > -1 && props.isCopy) {
          const insertItem = {
            ...form.value,
            id: getGenerateId(),
            driver: driverOption.value.find((item) => item.value === driverId)?.label,
          };
          store.currentEnvDetailInfo.config.dataSources.splice(index + 1, 0, insertItem);
        } else {
          const dataSourceItem = {
            ...form.value,
            id: getGenerateId(),
            driver: driverOption.value.find((item) => item.value === driverId)?.label,
          };
          store.currentEnvDetailInfo.config.dataSources.push(dataSourceItem);
        }
        formReset();
        showModal.value = false;
      } catch (error) {
        // eslint-disable-next-line no-console
        console.error(error);
      }
    }
  });
}
watch(() => showModal.value, (newValue) => {
  if (newValue) {
    if (props.currentId) {
      const currentItem = store.currentEnvDetailInfo.config.dataSources.find((item) => item.id === props.currentId);
      if (currentItem) {
        if (props.isCopy) {
          form.value = {
            ...currentItem,
            id: '',
            dataSource: `copy_${currentItem.dataSource}`.substring(0, 255),
          };
        } else {
          form.value = {
            ...currentItem,
          };
        }
      } else {
        formReset();
      }
    }
  }
})
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void;
  (e: 'addOrUpdate', data: DataSourceItem, cb: (v: boolean) => void): void;
}>();
const handleCancel = (shouldSearch: boolean) => {
  emit('cancel', shouldSearch);
  showModal.value = false;
  formReset();
};
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog"
           :mask-closable="false"
           @close="handleCancel(false)">
    <template #header>
      <span v-if="props.currentId && !props.isCopy">
        {{ `更新数据源{name}` }}
      </span>
      <span v-else>
        添加数据源
      </span>
    </template>
    <div>
      <n-form ref="formRef" :model="form">
        <n-form-item path="dataSource" label="数据源名称" :rule="{required:true,message:'数据源名称不能为空'}">
          <n-input v-model:value="form.dataSource" :maxlength="255" clearable placeholder="请输入数据源名称"/>
        </n-form-item>
        <n-form-item path="driverId" label="驱动" :rule="{required:true,message:'驱动必填'}">
          <n-select v-model:value="form.driverId" :options="driverOption"/>
        </n-form-item>
        <n-form-item path="dbUrl" label="数据库连接 URL" :rule="{required:true,message:'数据库连接 URL 必填'}">
          <n-input v-model:value="form.dbUrl" :maxlength="255" clearable placeholder="请输入数据库连接 URL"/>
        </n-form-item>
        <n-form-item path="username" label="用户名" :rule="{required:true,message:'用户名不能为空'}">
          <n-input v-model:value="form.username" :maxlength="255" clearable placeholder="请输入用户名"/>
        </n-form-item>
        <n-form-item path="password" label="密码" :rule="{required:true,message:'密码不能为空'}">
          <n-input v-model:value="form.password" :maxlength="255" clearable placeholder="请输入密码"/>
        </n-form-item>
        <n-form-item path="poolMax" label="最大连接数" :rule="{required:true,message:'最大连接数必填'}">
          <n-input-number v-model:value="form.poolMax" :min="1" :max="10000" :default-value="1"/>
        </n-form-item>
        <n-form-item path="timeout" label="超时时间" :rule="{required:true,message:'超时时间必填'}">
          <n-input-number v-model:value="form.timeout" :min="0" :max="600000" :step="100" :default-value="1000"/>
        </n-form-item>
        <n-button>测试连接</n-button>
      </n-form>
    </div>
    <template #action>
      <div class="flex flex-row gap-[14px]">
        <n-button secondary @click="handleCancel(false)">取消</n-button>
        <n-button type="primary" @click="handleSubmit">{{
            props.currentId && !props.isCopy ? '确认' : '添加'
          }}
        </n-button>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>