<script setup lang="ts">

import TopMenu from "/@/layout/components/header/TopMenu.vue";
import {useAppStore, useUserStore} from "/@/store";
import {getFirstRouteNameByPermission, hasAnyPermission} from "/@/utils/permission.ts";
import {projectApi} from "/@/api/methods/project.ts";

const appStore = useAppStore();
const userStore = useUserStore();
const router = useRouter();
const handleSelectProject = async (value: string) => {
  try {
    appStore.showLoading();
    await projectApi.switchProject({projectId: value, userId: userStore.id || ''});
    await router.replace({
      name: getFirstRouteNameByPermission(router.getRoutes()),
      query: {
        orgId: appStore.currentOrgId,
        pId: value,
      },
    });
  } catch (e) {
  } finally {
    appStore.hideLoading();
  }
}
watch(
    () => appStore.currentOrgId,
    async () => {
      await appStore.initProjectList();
    },
    {immediate: true}
);
</script>

<template>
  <n-layout-header bordered class="nav" style="grid-template-columns: calc(272px - var(--side-padding)) 1fr auto;">
    <n-text class="ui-logo">
      <img src="/favicon.svg" alt="logo"/>
      <span class="bold">Nuts</span>
    </n-text>
    <div class="flex items-center">
      <div class="w-[216px] ml-[24px] mr-[12px] shrink-0">
        <n-select v-model:value="appStore.currentProjectId" :options="appStore.projectList" value-field="id"
                  label-field="name"
                  @update:value="handleSelectProject">
          <template v-if="hasAnyPermission(['ORGANIZATION_PROJECT:READ+ADD'])" #header>
            <n-button text class="select-header-button mb-[4px] h-[28px] w-full justify-start pl-[7px] pr-0">
              <template #icon>
                <div class="i-mdi:plus-circle-outline"/>
              </template>
              新建项目
            </n-button>
          </template>
        </n-select>
      </div>
      <div class="nav-menu">
        <top-menu/>
      </div>
    </div>
    <div class="nav-end">
      <n-button type="info" text class="nav-picker">
        <template #icon>
          <div class="i-mdi:calendar-clock"/>
        </template>
      </n-button>
      <n-button type="success" text class="nav-picker">
        <template #icon>
          <div class="i-carbon:notification"/>
        </template>
      </n-button>
      <n-text class="nav-picker padded">1.x</n-text>
    </div>
  </n-layout-header>
</template>

<style scoped>
.nav {
  display: grid;
  grid-template-rows: calc(var(--header-height) - 1px);
  padding: 0 var(--side-padding);
}

.nav, .ui-logo {
  align-items: center;
}

.ui-logo {
  cursor: pointer;
  display: flex;
  font-size: 18px
}

.nav-menu {
  flex-grow: 0;
  flex-shrink: 1;
  overflow: hidden;
  padding-left: 36px
}

.nav-end {
  align-items: center;
  display: flex
}

.nav-picker {
  margin-right: 4px
}

.nav-picker.padded {
  padding: 0 10px
}

.nav-picker:last-child {
  margin-right: 0
}

.ui-logo > img {
  height: 32px;
  margin-right: 12px;
  width: 32px
}
</style>