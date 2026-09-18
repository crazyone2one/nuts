<script setup lang="ts">
import useProjectEnvStore from "/@/store/modules/useProjectEnvStore.ts";
import type {FtpItem} from "/@/types/environmental.ts";

const store = useProjectEnvStore();
const ftpConfig = computed({
  get: () => store.currentEnvDetailInfo.config.ftpConfig || {},
  set: (value: FtpItem) => {
    store.currentEnvDetailInfo.config.ftpConfig = value || {};
  },
});
</script>

<template>
  <div>
    <div class="flex flex-row items-center gap-[8px]">
      <span>FTP 配置</span>
    </div>
    <div class="mt-[8px]">
      <n-form :model="ftpConfig" size="small">
        <n-grid :cols="24" :x-gap="24">
          <n-form-item-gi :span="12" path="ip" label="IP">
            <n-input v-model:value="ftpConfig.ip" placeholder="请输入 IP 地址"/>
          </n-form-item-gi>
          <n-form-item-gi :span="12" path="port" label="端口">
            <n-input v-model:value="ftpConfig.port" placeholder="请输入端口，默认8841"/>
          </n-form-item-gi>
          <n-form-item-gi :span="12" path="username" label="用户名">
            <n-input v-model:value="ftpConfig.username" placeholder="请输入用户名"/>
          </n-form-item-gi>
          <n-form-item-gi :span="12" path="password" label="密码">
            <n-input v-model:value="ftpConfig.password" placeholder="请输入密码"/>
          </n-form-item-gi>
          <n-form-item-gi :span="12" path="localPath" label="本地路径">
            <n-input v-model:value="ftpConfig.localPath" placeholder="请输入本地路径,/app/files/"/>
          </n-form-item-gi>
          <n-form-item-gi :span="12" path="remotePath" label="远程路径">
            <n-input v-model:value="ftpConfig.remotePath" placeholder="请输入远程路径，/home/app/files/ftp/"/>
          </n-form-item-gi>
          <n-form-item-gi :span="24" path="description" label="描述">
            <n-input v-model:value="ftpConfig.description" placeholder="请输入描述,"/>
          </n-form-item-gi>
        </n-grid>
      </n-form>
    </div>
  </div>
</template>

<style scoped>

</style>