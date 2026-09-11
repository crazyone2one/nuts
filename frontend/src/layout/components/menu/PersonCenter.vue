<script setup lang="ts">
import {type DropdownOption, NAvatar} from "naive-ui";
import {authApi} from "/@/api/methods/auth.ts";
import router from "/@/router";
import type {LocationQueryRaw} from "vue-router";
import {useAppStore, useUserStore} from "/@/store";
import {tokenManager} from "/@/utils/token.ts";

const logoUrl = 'https://picsum.photos/200/200'
const appStore = useAppStore();

const options: Array<DropdownOption> = [
  {
    label: '个人中心',
    key: 'stmt1',
  },
  {
    label: '退出系统',
    key: 'stmt2'
  },
]
const handleSelectPersonCenter = (key: string) => {
  switch (key) {
    case 'stmt1':
      // Handle personal center selection
      break;
    case 'stmt2':
      window.$dialog.warning({
        content: '确定要退出系统吗？',
        positiveText: '确定',
        negativeText: '取消',
        onPositiveClick() {
          authApi.logout().then(async () => {
            console.log('执行退出')
            const route = router.currentRoute.value;
            await router.replace({
              name: 'login',
              query: {
                redirect: route.fullPath !== '/login' ? route.fullPath : undefined,
                ...route.query,
              } as LocationQueryRaw,
            });

            const userStore = useUserStore()
            userStore.$reset()
            tokenManager.clear();
          }).catch(()=>{
            console.log('1')
          })
        }
      })
      break;
  }
}
</script>

<template>
  <n-dropdown trigger="click" :options="options" placement="right" @select="handleSelectPersonCenter">
    <n-flex justify="center" class="shadow-xl bg-indigo-300 opacity-50 rounded-lg " style="padding: 4px 6px;">
      <n-avatar round style="margin-right: 10px;" :src="logoUrl"/>
      <div v-if="!appStore.menuCollapse" class="cursor-pointer">
        <div>打工仔</div>
        <div class="text-xs  font-medium">你是办公室里最亮的星</div>
      </div>
    </n-flex>
  </n-dropdown>
</template>

<style scoped>

</style>