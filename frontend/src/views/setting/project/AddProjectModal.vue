<script setup lang="ts">
import type {editType} from "/@/types/project.ts";
import {useForm} from "alova/client";
import {projectApi} from "/@/api/methods/project.ts";
import {useAppStore, useUserStore} from "/@/store";
import UserSelector from '/@/components/user-selector/index.vue'
import {UserRequestTypeEnum} from "/@/components/user-selector/utils.ts";
import type {FormInst} from "naive-ui";
import {OrgOption} from "/@/api/constants.ts";

const showModal = defineModel<boolean>({default: false})
const props = defineProps<{ currentProject?: editType; }>();
const userStore = useUserStore()
const appStore = useAppStore()
const formRef = ref<FormInst | null>(null)
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void;
}>();
const isEdit = computed(() => !!props.currentProject?.id);
const {form, loading, send} = useForm(formData => projectApi.createOrUpdateProject(formData), {
  initialForm: {
    name: '',
    num: '',
    userIds: [],
    organizationId: '',
    description: '',
    enable: true,
  },
  resetAfterSubmiting: true,
})
const formReset = () => {
  form.name = '';
  form.userIds = userStore.id ? [userStore.id] : [];
  form.organizationId = '';
  form.description = '';
  form.enable = true;
}
const rules = {
  name: [{required: true, message: '项目名称不能为空', trigger: 'blur'}],
  num: [{required: true, message: '项目编号不能为空', trigger: 'blur'}],
  organizationId: [{required: true, message: '所属组织不能为空', trigger: 'blur'}],
  userIds: [{type: 'array', required: true, message: '项目管理员不能为空', trigger: 'blur'}],
}
const handleSave = () => {
  formRef.value?.validate(err => {
    if (!err) {
      send().then(() => {
        window.$message.success(`${isEdit.value ? '更新' : '创建'}项目成功`)
        handleCancel(true);
        appStore.initProjectList();
      })
    }
  });
}
const handleCancel = (shouldSearch: boolean) => {
  formRef.value?.restoreValidation()
  emit('cancel', shouldSearch)
};
const initAffiliatedOrgOption = () => {
  form.value.organizationId = OrgOption[0].value;
}
watchEffect(() => {
  initAffiliatedOrgOption()
  if (props.currentProject?.id) {
    if (props.currentProject) {
      form.id = props.currentProject.id;
      form.name = props.currentProject.name;
      form.description = props.currentProject.description;
      form.enable = props.currentProject.enable;
      form.userIds = props.currentProject.userIds;
      form.organizationId = props.currentProject.organizationId;
    }
  } else {
    formReset()
  }
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" :mask-closable="false"
           @close="handleCancel(false)">
    <template #header>
      <div v-if="isEdit">
        更新项目
        <span>{{ props.currentProject?.name }}</span>
      </div>
      <span v-else>创建项目</span>
    </template>
    <div class="form">
      <n-form ref="formRef" :model="form" :rules="rules">
        <n-form-item path="name" label="项目名称">
          <n-input v-model:value="form.name" :maxlength="255" placeholder="请输入项目名称，不可与其他项目名称重复"/>
        </n-form-item>
        <n-form-item path="num" label="项目编号">
          <n-input v-model:value="form.num" :maxlength="255" placeholder="请输入项目编号，不可与其他项目编号重复"/>
        </n-form-item>
        <n-form-item path="organizationId" label="所属组织">
          <n-select v-model:value="form.organizationId" :options="OrgOption" placeholder="请选择所属组织" disabled/>
        </n-form-item>
        <n-form-item path="userIds" label="项目管理员">
          <user-selector v-model:user-ids="form.userIds" :type="UserRequestTypeEnum.SYSTEM_PROJECT_ADMIN"
                         placeholder="请选择项目管理员"/>
        </n-form-item>
        <n-form-item path="description" label="描述">
          <n-input v-model:value="form.description" type="textarea" placeholder="请对该项目进行描述"
                   :maxlength="1000" clearable
                   :autosize="{minRows:1}"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <n-flex>
        <n-button secondary :disabled="loading" @click="handleCancel(false)">取消</n-button>
        <n-button type="primary" :loading="loading" @click="handleSave">{{ isEdit ? '编辑' : '创建' }}</n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>