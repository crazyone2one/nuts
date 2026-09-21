<script setup lang="ts">
import type {InputInst} from "naive-ui";

interface OnUpdateValue {
  (value: string): void
}

const props = defineProps<{
  value: string;
  onUpdateValue: OnUpdateValue
  isEdit?: boolean
  size?: 'tiny' | 'small' | 'medium' | 'large'
}>()
const editing = ref(props.isEdit ?? false)
const inputRef = ref<InputInst | null>(null)
const inputValue = ref(props.value)
watch(() => props.isEdit, (value) => {
  editing.value = value ?? false
  if (editing.value) {
    nextTick(() => inputRef.value?.focus())
  }
})
watch(() => props.value, (value) => {
  inputValue.value = value
})
const handleOnClick = () => {
  editing.value = true
  nextTick(() => {
    inputRef.value?.focus()
  })
}
const handleChange = () => {
  props.onUpdateValue?.(String(inputValue.value))
  editing.value = false
}
</script>

<template>
  <div @click="handleOnClick">
    <n-input v-if="editing" ref="inputRef"
             v-model:value="inputValue"
             @update:value="v=>inputValue = v"
             :size="props.size"
             @blur="handleChange"/>
    <span v-else>{{ props.value }}</span>
  </div>
</template>

<style scoped>

</style>