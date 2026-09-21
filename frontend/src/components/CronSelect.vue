<script setup lang="ts">
const {size = 'medium'} = defineProps<{ size?: 'tiny' | 'small' | 'medium' | 'large' }>()
const options = [
  {label: '(每小时)', value: '0 0 0/1 * * ?'},
  {label: '(每 6 小时)', value: '0 0 0/6 * * ?'},
  {label: '(每 12 小时)', value: '0 0 0/12 * * ?'},
  {label: '(每天)', value: '0 0 0 * * ?'},
]
const cron = defineModel<string>('modelValue', {required: true});
const loading = defineModel<boolean>('loading', {required: false});
const emit = defineEmits<{
  (
      e: 'change',
      value: string
  ): void;
}>();
const handleUpdateValue = (value: string) => {
  emit('change', value);
}
</script>

<template>
  <n-select v-model:value="cron" :options="options" :size="size" :loading="loading"
            tag filterable
            placeholder="可直接输入表达式"
            @update:value="handleUpdateValue"
  />
</template>

<style scoped>

</style>