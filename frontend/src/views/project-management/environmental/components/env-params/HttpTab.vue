<script setup lang="ts">

</script>

<template>
  <div class="flex items-center justify-between">
    <n-button v-permission="['PROJECT_ENVIRONMENT:READ+UPDATE']">
      添加 HTTP
    </n-button>
    <div class="flex items-center justify-end gap-[8px]">
      <n-input-number :min="0" :max="600000" :step="100" class="w-[200px]">
        <template #prefix>
          连接超时(ms):
        </template>
      </n-input-number>
      <n-input-number :min="0" :max="600000" :step="100" class="w-[200px]">
        <template #prefix>
          响应超时(ms):
        </template>
      </n-input-number>
    </div>
  </div>
</template>

<style scoped>

</style>