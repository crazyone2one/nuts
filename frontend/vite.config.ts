import vue from '@vitejs/plugin-vue'
import {defineConfig, loadEnv} from 'vite'
import UnoCSS from 'unocss/vite'
import Components from 'unplugin-vue-components/vite'
import {NaiveUiResolver} from 'unplugin-vue-components/resolvers'
import AutoImport from 'unplugin-auto-import/vite'
import path from "node:path";
// https://vite.dev/config/
export default defineConfig(({mode}) => {
    const env = loadEnv(mode, process.cwd(), '')
    return {
        resolve: {
            alias: [{
                find: /\/@\//,
                replacement: path.resolve(import.meta.dirname, '.', 'src') + '/',
            }],
            dedupe: ['vue'],
        },
        plugins: [vue(), UnoCSS(),
            // https://github.com/unplugin/unplugin-vue-components#configuration
            Components({
                dts: true,
                resolvers: [NaiveUiResolver()]
            }),
            AutoImport({
                imports: ['vue', 'pinia', 'vue-router',
                    {'naive-ui': ['useDialog', 'useMessage', 'useNotification', 'useLoadingBar']}
                ]
            })],
        server: {
            proxy: {
                '^/front/.*': {
                    target: 'http://localhost:8080',
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/front/, ''),
                },
            }
        },
        build: {
            reportCompressedSize: false,
            chunkSizeWarningLimit: 2000,
            rolldownOptions: {
                output: {
                    codeSplitting: {
                        groups: [
                            {test: /node_modules\/naive-ui/, name: 'naive-ui',},
                            {test: /node_modules\/vue-router/, name: 'vue-router',},
                            {test: /node_modules\/vue/, name: 'vue',},
                        ]
                    }
                }
            }
        },
        define: {
            __APP_ENV__: JSON.stringify(env.APP_ENV),
        },
    }
})
