<script setup lang="ts">
import useProjectEnvStore from "/@/store/modules/useProjectEnvStore.ts";
import type {EnvConfigItem} from "/@/types/environmental.ts";

const store = useProjectEnvStore();
const currentList = computed({
  get: () => store.currentEnvDetailInfo.config.hostConfig || {},
  set: (value: EnvConfigItem) => {
    store.currentEnvDetailInfo.config.hostConfig = value || {};
  },
});
</script>

<template>
  <div>
    <div class="flex flex-row items-center gap-[8px]">
      <n-switch v-model:value="currentList.enable" size="small"/>
      <span>Host 配置</span>
    </div>
    <div class="mt-[8px]">
      <n-form>
        <n-form-item path="ip" label="IP">
          <n-input placeholder="请输入 IP 地址"/>
        </n-form-item>
        <n-form-item path="domain" label="域名">
          <n-input placeholder="请输入域名"/>
        </n-form-item>
        <n-form-item path="description" label="描述">
          <n-input placeholder="请输入描述,/home/app/files/ftp/"/>
        </n-form-item>
      </n-form>
    </div>
  </div>
</template>

<style scoped>

</style>