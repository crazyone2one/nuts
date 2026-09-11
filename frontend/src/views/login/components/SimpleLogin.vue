<script setup lang="ts">
import {addGlobalStyles, initParticles} from "/@/views/login/components/particles.ts";
import {useForm} from "alova/client";
import {authApi} from "/@/api/methods/auth.ts";
import type {FormInst} from "naive-ui";
import {useAppStore, useUserStore} from "/@/store";
import {getFirstRouteNameByPermission, routerNameHasPermission} from "/@/utils/permission.ts";

const appStore = useAppStore();
const userStore = useUserStore();
const router = useRouter();
const formRef = ref<FormInst | null>(null);
const isDarkMode = ref<boolean>(window.matchMedia('(prefers-color-scheme: dark)').matches);
const {form, loading, send} = useForm(formData => authApi.login(formData), {
  initialForm: {
    username: "",
    password: "",
  },
  resetAfterSubmiting: true
})
const handleLogin = () => {
  formRef.value?.validate(err => {
    if (!err) {
      try {
        send().then(res => {
          const {accessToken, lastOrganizationId, lastProjectId} = res
          localStorage.setItem("accessToken", accessToken)
          appStore.setCurrentOrgId(lastOrganizationId || '');
          appStore.setCurrentProjectId(lastProjectId || '');
          userStore.setInfo(res)
          window.$message.success('登录成功')
          const {redirect, ...othersQuery} = router.currentRoute.value.query;
          const redirectHasPermission =
              redirect && routerNameHasPermission(redirect as string, router.getRoutes());
          const currentRouteName = getFirstRouteNameByPermission(router.getRoutes());
          router.push({
            name: redirectHasPermission ? (redirect as string) : currentRouteName,
            query: {
              ...othersQuery,
              orgId: appStore.currentOrgId,
              pId: appStore.currentProjectId,
            },
          });
        })
      } catch (err) {
        localStorage.removeItem('accessToken')
        throw err;
      }
    }
  })
}
onMounted(() => {
  addGlobalStyles();
  initParticles(isDarkMode.value);

  // 检查深色模式偏好
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark');
  }
});
</script>

<template>
  <div class="login-page">
    <!-- 粒子背景层 -->
    <div id="particles-js" class=" inset-0 z-0"/>
    <!-- 背景渐变遮罩 -->
    <div class=" inset-0 bg-gradient-to-br from-primary/10 to-primary/5 z-10"/>
    <!-- 登录框容器 -->
    <div class="login-container">
      <n-card :bordered="false"
              title="欢迎登录"
              class="login-card">
        <n-form ref="formRef" :model="form" size="large" :show-label="false">
          <n-form-item label="用户名" path="username">
            <n-input v-model:value="form.username" placeholder="请输入用户名">
              <template #prefix>
                <span class="i-mdi:account-circle-outline"/>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item label="密码" path="password">
            <n-input v-model:value="form.password" type="password" show-password-on="click"
                     placeholder="请输入密码" :maxlength="64">
              <template #prefix>
                <span class="i-mdi:shield-account-outline"/>
              </template>
            </n-input>
          </n-form-item>
          <n-button type="primary"
                    class="w-full h-11 text-base bg-gradient-to-r transition-all duration-300 shadow-md"
                    @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </n-button>
        </n-form>
      </n-card>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  --primary: #6366f1; /* 主色调：靛蓝色 */
  --primary-light: #8b5cf6; /* 亮一点的主色调：紫色 */
  --text-200: #64748b; /* 次要文本颜色 */
  --text-300-dark: #cbd5e1; /* 深色模式下的次要文本 */
  --card: #ffffff; /* 卡片背景色 */
  --card-dark: #1e293b; /* 深色模式卡片背景 */
  --input-dark: #334155; /* 深色模式输入框 */
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  /* 可选：给页面加一个淡淡的渐变背景，让卡片更突出 */
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 20px;
  border-radius: 16px; /* 大圆角更现代 */

  /* 核心：多层阴影营造立体悬浮感 */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08), /* 大范围柔和阴影 */ 0 2px 8px rgba(0, 0, 0, 0.04); /* 贴近卡片的阴影 */

  /* 核心：玻璃拟态质感 (可选) */
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);

  /* 增加过渡动画，鼠标悬停时轻微上浮 */
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12),
  0 4px 10px rgba(0, 0, 0, 0.06);
}

.login-btn {
  margin-top: 10px;
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}
</style>