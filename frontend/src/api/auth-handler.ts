import {useUserStore} from "/@/store";
import {createServerTokenAuthentication} from "alova/client";
import {authApi} from "/@/api/methods/auth.ts";
import {tokenManager} from "/@/utils/token.ts";
import {RESPONSE_CODE} from "/@/api/constants.ts";
import router from "/@/router";
import type {LocationQueryRaw} from "vue-router";

let refreshing: Promise<string> | null = null
export const {onAuthRequired, onResponseRefreshToken} = createServerTokenAuthentication({
    refreshTokenOnSuccess: {
        isExpired: async (response, method) => {
            // 仅 401 才需要判断是否过期；非 401 直接返回，避免对空响应体（如 204 No Content）调用 json() 抛错
            if (response.status !== 401) return false;
            const res = await response.clone().json();
            const isExpired = method.meta && method.meta.isExpired;
            return res.code === RESPONSE_CODE.TOKEN_EXPIRED && !isExpired;
        },
        handler: async (_response, method) => {
            method.meta = method.meta || {};
            method.meta.isExpired = true;
            try {
                refreshing ??= authApi.refresh().then(({accessToken}) => {
                    tokenManager.set(accessToken);
                    return accessToken;
                }).finally(() => {
                    refreshing = null
                })
                await refreshing;
            } catch (error) {
                // token刷新失败，跳转回登录页
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
                // 并抛出错误
                throw error;
            }
        },
        metaMatches: {
            refreshToken: true
        },
    },
    assignToken: method => {
        // 登录/免鉴权/刷新请求不携带旧 token
        if (method.meta?.ignoreToken || method.meta?.isVisitor || method.meta?.refreshToken) return;
        const token = tokenManager.get();
        if (token) {
            method.config.headers.Authorization = `Bearer ${token}`;
        }
    },

    logout: {
        metaMatches: {
            logout: true
        },
        handler: async (_response, _method) => {
            tokenManager.clear();
        }
    },
    visitorMeta: {
        isVisitor: true
    }
});