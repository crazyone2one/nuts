<script setup lang="ts">
import SiderMenu from '/@/layout/components/menu/index.vue'
import PageHeader from '/@/layout/components/header/index.vue'
import {useAppStore} from "/@/store";

const appStore = useAppStore();
</script>

<template>
  <n-layout class="h-full" position="absolute">
    <page-header/>
    <n-layout has-sider position="absolute" style="top: 64px;" :style="`bottom: ${appStore.showFooter ? 64 : 0}px;`">
      <sider-menu/>
      <n-layout-content content-style="padding: 24px;" :native-scrollbar="false">
        <n-spin size="large" :show="appStore.loading" :description="appStore.loadingTip">
          <router-view/>
        </n-spin>
      </n-layout-content>
    </n-layout>
    <n-layout-footer v-if="appStore.showFooter" bordered position="absolute" style="height: 64px; padding: 24px">
      成府路
    </n-layout-footer>
  </n-layout>
</template>

<style scoped>

</style>