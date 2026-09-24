<script setup lang="ts">
import type {UploadFileInfo} from "naive-ui";

interface Props {
  /** 接受的文件类型，如 'image/*,.pdf,.doc' */
  accept?: string;
  /** 是否支持多文件上传 */
  multiple?: boolean;
  /** 最大上传文件数量 */
  maxNumber?: number;
  /** 单个文件最大大小（字节），默认 10MB */
  maxFileSize?: number;
  /** 是否禁用 */
  disabled?: boolean;
  /** 上传列表的展示样式，默认 text */
  listType?: 'text' | 'image' | 'image-card';
  /** 自定义上传方法，接收 File 对象，返回 Promise<string>（resolve 为文件 URL） */
  customUpload?: (file: File) => Promise<string>;
}

const props = withDefaults(defineProps<Props>(), {
  accept: 'image/*,.pdf,.doc,.docx,.xls,.xlsx,.zip',
  multiple: true,
  maxNumber: 5,
  maxFileSize: 10 * 1024 * 1024,
  disabled: false,
  listType: 'text',
  customUpload: undefined
});
const emit = defineEmits<{
  (e: 'update:value', value: string[]): void;
  (e: 'success', data: { file: UploadFileInfo; url: string }): void;
  (e: 'error', data: { file: UploadFileInfo; error: any }): void;
}>();
const modelValue = defineModel<string[]>('value', {required: true, default: () => []});
// 组件内部使用的文件列表
const fileList = ref<UploadFileInfo[]>([]);
// 初始化：将已有的 URL 列表转换为 UploadFileInfo 列表（用于回显）
const initFileList = () => {
  if (modelValue.value && modelValue.value.length > 0) {
    fileList.value = modelValue.value.map((url, index) => ({
      id: `file-${index}-${Date.now()}`,
      name: url.split('/').pop() || `file-${index}`,
      status: 'finished',
      url
    }));
  }
};
initFileList();
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`;
};
const handleBeforeUpload = ({file}: { file: UploadFileInfo }) => {
  if (!file.file) {
    return false;
  }
  // 校验文件大小
  if (file.file.size > props.maxFileSize) {
    window.$message.error(`文件 "${file.name}" 大小超过限制（最大 ${formatFileSize(props.maxFileSize)}）`);
    return false;
  }
  // 校验文件类型
  const acceptedTypes = props.accept.split(',').map(type => type.trim());
  const fileTypeMatches = acceptedTypes.some(type => {
    if (type.endsWith('/*')) {
      const prefix = type.split('/*')[0];
      return file.type?.startsWith(prefix);
    }
    if (type.startsWith('.')) {
      return file.name?.toLowerCase().endsWith(type.toLowerCase());
    }
    return type === file.type;
  });
  if (!fileTypeMatches) {
    window.$message.error(`文件 "${file.name}" 类型不支持，请上传 ${props.accept} 格式的文件`);
    return false;
  }
  if (props.multiple && fileList.value.length >= props.maxNumber) {
    window.$message.error(`最多只能上传 ${props.maxNumber} 个文件`);
    return false;
  }
  return true;
}
// 文件列表变化时，同步更新 v-model 的值
const handleUpdateFileList = (currentFileList: UploadFileInfo[]) => {
  fileList.value = currentFileList;
  const urls = currentFileList
      .filter(item => item.status === 'finished' && item.url)
      .map(item => item.url!);
  emit('update:value', urls);
};
// 自定义上传请求（手动点击按钮后，对 pending 状态的文件逐个调用）
// const handleCustomRequest = ({file, onFinish, onError}: UploadCustomRequestOptions) => {
//   if (!props.customUpload || !file.file) {
//     onError();
//     return;
//   }
//   props.customUpload(file.file)
//       .then(url => {
//         file.url = url;
//         emit('success', {file, url});
//         onFinish();
//       })
//       .catch(error => {
//         window.$message.error(`文件 "${file.name}" 上传失败`);
//         emit('error', {file, error});
//         onError();
//       });
// };

const handleManualUpload = async () => {
  if (!props.customUpload) {
    window.$message.error('未提供自定义上传方法');
    return;
  }
  const pendingFiles = fileList.value.filter(item => item.status === 'pending');
  if (pendingFiles.length === 0) {
    window.$message.warning('没有待上传的文件');
    return;
  }
  // 遍历待上传文件，手动调用 customUpload
  for (const file of pendingFiles) {
    if (!file.file) continue;

    // 将状态改为 uploading
    file.status = 'uploading';

    try {
      const url = await props.customUpload(file.file);
      file.status = 'finished';
      file.url = url;
      emit('success', {file, url});
      window.$message.success(`文件 "${file.name}" 上传成功`);
    } catch (error) {
      file.status = 'error';
      emit('error', {file, error});
      window.$message.error(`文件 "${file.name}" 上传失败`);
    }
  }

  // 触发列表更新，同步 v-model
  handleUpdateFileList(fileList.value);
};
defineExpose({handleManualUpload})
</script>

<template>
  <div>
    <n-upload ref="uploadRef" v-model:file-list="fileList"
              :accept="accept"
              :multiple="multiple"
              :disabled="disabled"
              :list-type="listType"
              :show-file-list="true"
              @before-upload="handleBeforeUpload"
              @update:file-list="handleUpdateFileList">
      <n-upload-dragger>
        <div style="margin-bottom: 12px">
          <n-icon size="48" :depth="3">
            <div class="i-mdi:archive-arrow-down-outline" />
          </n-icon>
        </div>
        <n-text style="font-size: 16px">
          点击或者拖动文件到该区域来上传
        </n-text>
        <n-p depth="3" style="margin: 8px 0 0 0">
          请不要上传敏感数据，比如你的银行卡号和密码，信用卡号有效期和安全码
        </n-p>
      </n-upload-dragger>
    </n-upload>
  </div>
</template>

<style scoped>
.uploaded-file-list {
  margin-top: 8px;
}

.uploaded-file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 14px;
  color: #666;
}

.success-tip {
  color: #18a058;
  margin-left: 8px;
}

.error-tip {
  color: #d03050;
  margin-left: 8px;
}

.uploading-tip {
  color: #2080f0;
  margin-left: 8px;
  font-size: 12px;
}

.pending-tip {
  color: #f0a020;
  margin-left: 8px;
  font-size: 12px;
}
</style>