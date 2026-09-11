<script setup lang="ts">
import {useRequest} from "alova/client";
import {userApi} from "/@/api/methods/user.ts";

const {send} = useRequest(() => userApi.getUserList(), {immediate: false})
const handleLoadUser = () => {
  send();
}
</script>

<template>
  <n-result
      title="setting user View"
      description="写好报告，搞乱项目，增本降效，做大蛋糕"
  >
    <template #icon>
      <div style="width: 300px">
        <n-button type="info" @click="handleLoadUser">load user</n-button>
      </div>
    </template>
  </n-result>
</template>

<style scoped>

</style>