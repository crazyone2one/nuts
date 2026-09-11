// vite-env.d.ts
declare const __APP_VERSION__: string
interface ViteTypeOptions {
    // 添加这行代码，你就可以将 ImportMetaEnv 的类型设为严格模式，
    // strictImportMetaEnv: unknown
}

interface ImportMetaEnv {
    readonly VITE_APP_TITLE: string
    // 更多环境变量...
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}
declare module '*.vue' {
    import type {DialogProviderInst, MessageProviderInst, NotificationProviderInst,} from 'naive-ui';
    import {DefineComponent} from 'vue';
    global {
        interface Window {
            $message: MessageProviderInst;
            $dialog: DialogProviderInst;
            $notification: NotificationProviderInst;
        }
    }
    const component: DefineComponent<{}, {}, any>
    export default component
}