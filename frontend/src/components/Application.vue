<script setup lang="ts">
import {useDialog, useMessage, useNotification} from 'naive-ui'

window.$message = useMessage();
window.$dialog = useDialog();
window.$notification = useNotification();
</script>

<template>
  <slot name="default"/>
</template>

<style scoped>

</style>