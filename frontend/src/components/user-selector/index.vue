<script setup lang="ts">
import {initOptionsFunc, type UserRequestType, UserRequestTypeEnum} from "/@/components/user-selector/utils.ts";
import {useRequest} from "alova/client";
import type {SelectOption} from "naive-ui";

const {type = UserRequestTypeEnum.SYSTEM_USER_GROUP, loadOptionParams = {}} = defineProps<{
  type?: UserRequestType;
  loadOptionParams?: Record<string, any>;
  placeholder?: string;
}>();
const currentValue = defineModel<Array<string | number>>('userIds')

const optionsRef = ref<SelectOption[]>([])
const {send: loadList, loading} = useRequest(params => {
  const {type, keyword, ...rest} = params;
  return initOptionsFunc(type, {keyword, ...rest})
}, {immediate: false})
const handleSearch = (query: string) => {
  if (!query.length) {
    optionsRef.value = []
    return
  }
  loadList({...loadOptionParams, type: type}).then(res => {
    optionsRef.value = res.filter(item => ~item.name.indexOf(query))
  })
}
</script>

<template>
  <n-select v-model:value="currentValue" :options="optionsRef" multiple
            filterable
            clearable
            remote
            label-field="name" value-field="id" :loading="loading"
            :placeholder="placeholder"
            @search="handleSearch"/>
</template>

<style scoped>

</style>