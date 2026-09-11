import {createRouter, createWebHashHistory} from 'vue-router'
import appRoutes from "/@/router/routes";
import type {App} from "vue";
import {createRouterGuards} from "/@/router/guard.ts";


const routes = [
    {
        path: '/', component: () => import('/@/layout/index.vue'),
        redirect: '/dashboard',
        children: [
            // {path: '/dashboard', name: 'dashboard', component: () => import('/@/views/dashboard/index.vue')},
            ...appRoutes,
        ]
    },

    {path: '/login', name: 'login', component: () => import('/@/views/login/index.vue')},
    {path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('/@/views/base/NotFound.vue')},
]

const router = createRouter({
    history: createWebHashHistory(),
    routes,
    strict: true,
    scrollBehavior: () => ({left: 0, top: 0}),
})
export const setupRouter = (app: App) => {
    app.use(router);
    // 创建路由守卫
    createRouterGuards(router);
}
export default router;