import {createAlova} from "alova";
import adapterFetch from "alova/fetch";
import VueHook from 'alova/vue';
import {useAppStore} from "/@/store";
import {BusinessError, RESPONSE_CODE} from "/@/api/constants.ts";
import {onAuthRequired, onResponseRefreshToken} from "/@/api/auth-handler.ts";

export const instance = createAlova({
    baseURL: `${window.location.origin}/front`,
    statesHook: VueHook,
    requestAdapter: adapterFetch(),
    timeout: 300 * 1000,
    beforeRequest: onAuthRequired(method => {
        const appStore = useAppStore()
        method.config.headers = {
            ...(method.config.headers ?? {}),
            'ORGANIZATION': appStore.currentOrgId,
            'PROJECT': appStore.currentProjectId,
        };
    }),
    responded: onResponseRefreshToken({
        onSuccess: async (response, method) => {
            // 下载请求：先处理状态码，再返回 blob，避免先消费 body 导致 blob() 报错
            if (method.meta?.isDownload) {
                if (response.status >= 400) {
                    const json = await response.clone().json();
                    throw new Error(json?.message || response.statusText);
                }
                return response.blob();
            }

            // 无响应体（如 204 No Content）：直接返回 undefined，避免 response.json() 抛错
            if (response.status === 204 || response.status === 205) {
                return undefined;
            }

            const json = await response.json();
            if (response.status >= 400) {
                window.$message.error(json?.message || '系统错误')
                throw new Error(response.statusText);
            }
            if (json?.code !== RESPONSE_CODE.OK) {
                // 业务失败：抛出 BusinessError，由调用方决定提示，避免 onError 重复弹窗
                throw new BusinessError(json?.message || '请求失败', json?.code);
            }
            // 解析的响应数据将传给 method 实例的 transform 钩子函数
            return json.data;
        },
        onError: (err, method) => {
            console.error(`🚀 ~ Error ~ [${method.type}] - [${method.url}]`, err);
            // 业务错误已由调用方处理，这里只提示非预期的网络错误，避免重复提示
            if (err instanceof BusinessError) {
                return;
            }
            const tip = `[${method.type}] - [${method.url}] - ${err.message}`;
            window.$message.error(tip);
        },
    })
});